/*******************************************************************************
 * Copyright 2012 Alexandre d'Alton
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.openjira.jira;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import org.openjira.jira.model.JiraComment;
import org.openjira.jira.model.JiraFilter;
import org.openjira.jira.model.JiraIssue;
import org.openjira.jira.model.JiraPriority;
import org.openjira.jira.model.JiraProject;
import org.openjira.jira.model.JiraResolution;
import org.openjira.jira.model.JiraServer;
import org.openjira.jira.model.JiraServerInfo;
import org.openjira.jira.model.JiraStatus;
import org.openjira.jira.model.JiraType;
import org.openjira.jira.model.JiraVersion;
import org.openjira.jiraservice.JiraContentProvider;
import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;
import org.xmlrpc.android.XMLRPCFault;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class JiraConn extends Service {

	private static final String KEY_SERVER = "server_id";
	private ArrayList<JiraFilter> filters = new ArrayList<JiraFilter>();
	private HashMap<Integer, JiraType> types = new HashMap<Integer, JiraType>();
	private HashMap<Integer, JiraStatus> statuses = new HashMap<Integer, JiraStatus>();
	private HashMap<Integer, JiraPriority> priorities = new HashMap<Integer, JiraPriority>();
	private final HashMap<Integer, JiraResolution> resolutions = new HashMap<Integer, JiraResolution>();
	private ArrayList<JiraProject> projectList = new ArrayList<JiraProject>();
	JiraServerInfo serverInfo;
	ArrayList<JiraIssue> issueList;

	XMLRPCClient rpcClient;

	Object loginToken;

	LoginListener loginListener;

	public String JIRA_URI;
	public static final String RPC_PATH = "/rpc/xmlrpc";
	protected static final String TAG = "JiraConn";
	public String USER_NAME;
	public String PASSWORD;

	private JiraDB db;
	private final AtomicBoolean connected = new AtomicBoolean(false);

	public JiraConn(final JiraServer server) {
		this.JIRA_URI = server.getUrl();
		this.USER_NAME = server.getUser();
		this.PASSWORD = server.getPassword();
		if (server != null) {
			this.db = new JiraDB(JiraApp.get(), server.getName());
		}
		// final ContentResolver cr = getContentResolver();
		// cr.insert(JiraContentProvider.CONTENT_URI, args);
	}

	public void setServer(final JiraServer server) {
		this.JIRA_URI = server.getUrl();
		this.USER_NAME = server.getUser();
		this.PASSWORD = server.getPassword();
		this.db = new JiraDB(JiraApp.get(), server.getName());
		try {
			doLogin(false);
			// Save current server
			PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putInt(KEY_SERVER, server.get_id());
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public JiraConn() {

	}

	@Override
	public void onCreate() {
		super.onCreate();
		// Try to load default server
		final int serverId = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(KEY_SERVER, -1);
		if (serverId != -1) {
			final ContentResolver cr = getContentResolver();
			final String[] projection = new String[] { JiraServersDB.KEY_NAME, JiraServersDB.KEY_URL, JiraServersDB.KEY_USER, JiraServersDB.KEY_PASSWORD };
			final Cursor c = cr.query(JiraContentProvider.CONTENT_URI_SERVERS, projection, JiraServersDB.KEY_ID + "=" + serverId, null, null);
			if (c.moveToFirst()) {
				final JiraServer server = new JiraServer(c.getString(0), c.getString(1), c.getString(2), c.getString(3));
				setServer(server);
			}
		}
	}

	public Object getLoginToken() {
		return this.loginToken;
	}

	public void loadFilters() throws Exception {
		Object[] f;
		try {
			f = (Object[]) this.rpcClient.call("jira1.getFavouriteFilters", this.loginToken);
		} catch (final XMLRPCFault e) {
			// added compatibility for JIRA < 4.x; for more informations see
			// https://studio.plugins.atlassian.com/wiki/display/JRPC/JIRA+RPC+Plugin
			f = (Object[]) this.rpcClient.call("jira1.getSavedFilters", this.loginToken);
		}
		this.filters.clear();
		for (int i = 0; i < f.length; i++) {
			final JiraFilter filter = JiraFilter.fromMap((Map) f[i]);
			this.filters.add(filter);
			this.db.updateFilter(filter);
			if (this.loginListener != null) {
				this.loginListener.onSyncProgress(null, i, f.length);
			}
		}
	}

	public ArrayList<JiraFilter> getFilters() {
		return this.filters;
	}

	public void loadResolutions() throws Exception {
		final Object[] f = (Object[]) this.rpcClient.call("jira1.getResolutions", this.loginToken);
		for (int i = 0; i < f.length; i++) {
			final JiraResolution j = JiraResolution.fromMap((Map) f[i]);
			this.resolutions.put(j.getId(), j);
			this.db.updateResolution(j);
			if (this.loginListener != null) {
				this.loginListener.onSyncProgress(null, i, f.length);
			}
		}
	}

	public void loadStatuses() throws Exception {
		final Object[] f = (Object[]) this.rpcClient.call("jira1.getStatuses", this.loginToken);
		for (int i = 0; i < f.length; i++) {
			final Map status = (Map) f[i];
			final JiraStatus j = JiraStatus.fromMap(status, this.JIRA_URI);
			this.statuses.put(j.getId(), j);
			this.db.updateStatus(j);
			if (this.loginListener != null) {
				this.loginListener.onSyncProgress(null, i, f.length);
			}
		}
	}

	public String getStatusLabel(final int status) {
		final JiraStatus s = this.statuses.get(status);

		return s == null ? null : s.getName();
	}

	public String getStatusIcon(final int status) {
		final JiraStatus s = this.statuses.get(status);
		return s == null ? null : s.getIcon();
	}

	public Bitmap getStatusBitmap(final int status) {
		final JiraStatus s = this.statuses.get(status);
		return s == null ? null : s.getBitmap();
	}

	public void loadPriorities() throws Exception {
		final Object[] f = (Object[]) this.rpcClient.call("jira1.getPriorities", this.loginToken);
		for (int i = 0; i < f.length; i++) {
			final Map prio = (Map) f[i];
			final JiraPriority j = JiraPriority.fromMap(prio, this.JIRA_URI);
			this.priorities.put(j.getId(), j);
			this.db.updatePriority(j);
			if (this.loginListener != null) {
				this.loginListener.onSyncProgress(null, i, f.length);
			}
		}
	}

	public String getPriorityLabel(final int prio) {
		return this.priorities.get(prio) == null ? "Unknown" : this.priorities.get(prio).getName();
	}

	public String getPriorityIcon(final int prio) {
		return this.priorities.get(prio) == null ? "" : this.priorities.get(prio).getIcon();
	}

	public Bitmap getPriorityBitmap(final int prio) {
		return this.priorities.get(prio).getBitmap();
	}

	public void loadTypes() throws Exception {
		final Object[] f = (Object[]) this.rpcClient.call("jira1.getIssueTypes", this.loginToken);
		for (int i = 0; i < f.length; i++) {
			final Map prio = (Map) f[i];
			final JiraType j = JiraType.fromMap(prio, this.JIRA_URI);
			this.types.put(j.getId(), j);
			this.db.updateType(j);
			if (this.loginListener != null) {
				this.loginListener.onSyncProgress(null, i, f.length);
			}
		}
	}

	public String getTypeLabel(final int prio) {
		return this.types.get(prio) == null ? "Unknown" : this.types.get(prio).getName();
	}

	public String getTypeIcon(final int prio) {
		return this.types.get(prio) == null ? "" : this.types.get(prio).getIcon();
	}

	public Bitmap getTypeBitmap(final int prio) {
		return this.types.get(prio).getBitmap();
	}

	public void loadProjects() throws Exception {
		Object[] projects;
		try {
			projects = (Object[]) this.rpcClient.call("jira1.getProjectsNoSchemes", this.loginToken);
		} catch (final XMLRPCFault e) {
			// added compatibility for JIRA < 3.13; for more informations see
			// https://studio.plugins.atlassian.com/wiki/display/JRPC/JIRA+RPC+Plugin
			projects = (Object[]) this.rpcClient.call("jira1.getProjects", this.loginToken);
		}

		// Print projects
		for (int i = 0; i < projects.length; i++) {
			if (this.loginListener != null) {
				this.loginListener.onSyncProgress(null, i, projects.length);
			}
			final Map project = (Map) projects[i];
			final JiraProject p = JiraProject.fromMap(project);
			this.projectList.add(p);
			final ArrayList<JiraVersion> versions = getVersions(p.getTag());
			p.setVersions(versions);
			this.db.updateProject(p);
		}
	}

	public ArrayList<JiraProject> getProjects() {
		return this.projectList;
	}

	public interface LoginListener {
		public void onLoginComplete();

		public void onLoginError(Exception e);

		public void onSyncProgress(String message, int progress, int max);
	}

	/**
	 * Calls onLoginComplete immediately if already connected
	 * 
	 * @param listener
	 */
	public void setOnLoginListener(final LoginListener listener) {
		this.loginListener = listener;
		// If already connected let the listener know right away
		if (this.connected.get()) {
			listener.onLoginComplete();
		}
	}

	private void doLogin(final boolean force) throws Exception {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if ((JiraConn.this.JIRA_URI != null) && (JiraConn.this.JIRA_URI.length() < 1)) {
					return;
				}
				if ((JiraConn.this.USER_NAME != null) && (JiraConn.this.USER_NAME.length() < 1)) {
					return;
				}
				if ((JiraConn.this.PASSWORD != null) && (JiraConn.this.PASSWORD.length() < 1)) {
					return;
				}
				try {
					JiraConn.this.rpcClient = new XMLRPCClient(JiraConn.this.JIRA_URI + RPC_PATH);
				} catch (final Exception e) {
					e.printStackTrace();
					if (JiraConn.this.loginListener != null) {
						JiraConn.this.loginListener.onLoginError(e);
					}
					return;
				}
				try {
					JiraConn.this.loginToken = JiraConn.this.rpcClient.call("jira1.login", JiraConn.this.USER_NAME, JiraConn.this.PASSWORD);
				} catch (final Exception e) {
					e.printStackTrace();
					if (JiraConn.this.loginListener != null) {
						JiraConn.this.loginListener.onLoginError(e);
					}
				}

				JiraConn.this.filters = JiraConn.this.db.getFilters();

				if ((JiraConn.this.filters != null) && (JiraConn.this.filters.size() > 0) && !force) {
					JiraConn.this.projectList = JiraConn.this.db.getProjects();
					JiraConn.this.statuses = JiraConn.this.db.getStatuses();
					JiraConn.this.priorities = JiraConn.this.db.getPriorities();
					// resolutions = db.getResolutions();
					JiraConn.this.types = JiraConn.this.db.getTypes();
					if (JiraConn.this.loginListener != null) {
						JiraConn.this.loginListener.onLoginComplete();
					}
					JiraConn.this.connected.set(true);
					JiraConn.this.db.close();
				} else {
					try {
						if (JiraConn.this.loginListener != null) {
							JiraConn.this.loginListener.onSyncProgress("Syncing Filters", 0, 1);
						}
						loadFilters();
						if (JiraConn.this.loginListener != null) {
							JiraConn.this.loginListener.onSyncProgress("Syncing Projects", 0, 1);
						}
						loadProjects();
						if (JiraConn.this.loginListener != null) {
							JiraConn.this.loginListener.onSyncProgress("Syncing Statuses", 0, 1);
						}
						loadStatuses();
						if (JiraConn.this.loginListener != null) {
							JiraConn.this.loginListener.onSyncProgress("Syncing Priorities", 0, 1);
						}
						loadPriorities();
						if (JiraConn.this.loginListener != null) {
							JiraConn.this.loginListener.onSyncProgress("Syncing Resolutions", 0, 1);
						}
						loadResolutions();
						if (JiraConn.this.loginListener != null) {
							JiraConn.this.loginListener.onSyncProgress("Syncing Types", 0, 1);
						}
						loadTypes();
						JiraConn.this.serverInfo = getServerInfo();
						if (JiraConn.this.loginListener != null) {
							JiraConn.this.loginListener.onLoginComplete();
						}
						JiraConn.this.connected.set(true);
					} catch (final Exception e) {
						e.printStackTrace();
						if (JiraConn.this.loginListener != null) {
							JiraConn.this.loginListener.onLoginError(e);
						}
					}
				}
			}
		}).start();
	}

	public void createIssue(final String project, final String priority, final String type, final String assignee, final String reporter, final String summary,
			final String description) throws XMLRPCException {
		final HashMap<String, String> params = new HashMap<String, String>();
		params.put("summary", summary);
		params.put("project", project);
		params.put("type", type);
		params.put("priority", priority);
		params.put("description", description);
		params.put("assignee", this.USER_NAME);
		params.put("reporter", this.USER_NAME);

		this.rpcClient.call("jira1.createIssue", this.loginToken, params);
		// Toast.makeText(context, "issue created", Toast.LENGTH_SHORT).show();
		Log.v("test", "issue creation complete...");
	}

	public ArrayList<JiraIssue> loadIssues(final String filter) throws XMLRPCException {
		final Object[] issues = (Object[]) this.rpcClient.call("jira1.getIssuesFromFilter", this.loginToken, filter);
		this.issueList = new ArrayList<JiraIssue>();
		for (int i = 0; i < issues.length; i++) {
			final Map issue = (Map) issues[i];
			this.issueList.add(JiraIssue.fromMap(issue));
		}
		return this.issueList;
	}

	public ArrayList<JiraIssue> searchIssues(final String query) throws XMLRPCException {
		final Object[] issues = (Object[]) this.rpcClient.call("jira1.getIssuesFromTextSearch", this.loginToken, query);
		this.issueList = new ArrayList<JiraIssue>();
		for (int i = 0; i < issues.length; i++) {
			final Map issue = (Map) issues[i];
			this.issueList.add(JiraIssue.fromMap(issue));
		}
		return this.issueList;
	}

	public JiraIssue loadIssue(final String key) throws XMLRPCException {
		final Map issue = (Map) this.rpcClient.call("jira1.getIssue", this.loginToken, key);
		return JiraIssue.fromMap(issue);
	}

	public JiraProject getProject(final String projectId) {
		for (int i = 0; i < this.projectList.size(); i++) {
			if (this.projectList.get(i).getTag().equals(projectId)) {
				return this.projectList.get(i);
			}
		}
		return null;
	}

	public ArrayList<JiraComment> getComments(final String key) throws XMLRPCException {
		final Object[] comments = (Object[]) this.rpcClient.call("jira1.getComments", this.loginToken, key);
		if (comments.length > 0) {
			final ArrayList<JiraComment> ret = new ArrayList<JiraComment>();
			for (int i = 0; i < comments.length; i++) {
				final Map comment = (Map) comments[i];
				ret.add(JiraComment.fromMap(comment));
			}
			return ret;
		} else {
			return null;
		}
	}

	public ArrayList<JiraVersion> getVersions(final String projectKey) throws XMLRPCException {
		final Object[] versions = (Object[]) this.rpcClient.call("jira1.getVersions", this.loginToken, projectKey);
		if (versions.length > 0) {
			final ArrayList<JiraVersion> ret = new ArrayList<JiraVersion>();
			for (int i = 0; i < versions.length; i++) {
				final HashMap version = (HashMap) versions[i];
				ret.add(JiraVersion.fromMap(version));
			}
			return ret;
		} else {
			return null;
		}
	}

	public void addComment(final String key, final String comment) throws XMLRPCException {
		this.rpcClient.call("jira1.addComment", this.loginToken, key, comment);
	}

	public interface OnCommentAddedListener {
		public void onCommentAdded(boolean success);
	}

	public void addCommentAsync(final String key, final String comment, final OnCommentAddedListener listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					addComment(key, comment);
				} catch (final XMLRPCException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					listener.onCommentAdded(false);
					return;
				}
				listener.onCommentAdded(true);
			}
		}).start();
	}

	public ArrayList<JiraComment> getUser(final String search) throws XMLRPCException {
		final Map user = (Map) this.rpcClient.call("jira1.getUser", this.loginToken, search);
		Log.v("Test", "got User: " + user);
		return null;
	}

	public JiraServerInfo getServerInfo() throws XMLRPCException {
		final Map map = (Map) this.rpcClient.call("jira1.getServerInfo", this.loginToken);

		final JiraServerInfo info = JiraServerInfo.fromMap(map);
		Log.v("test", "Server info : " + map);
		return info;
	}

	public ArrayList<JiraIssue> getIssueList() {
		return this.issueList;
	}

	public ArrayList<String> getProjectsLabels(final ArrayList<String> ids) {
		final ArrayList<String> ret = new ArrayList<String>();
		for (int i = 0; i < this.projectList.size(); i++) {
			if (this.projectList.get(i) != null) {
				ret.add(this.projectList.get(i).getName());
				if (ids != null) {
					ids.add(this.projectList.get(i).getTag());
				}
			}
		}
		return ret;
	}

	public ArrayList<String> getTypeLabels(final ArrayList<Integer> ids) {
		final ArrayList<String> ret = new ArrayList<String>();
		for (int i = 0; i < this.types.size(); i++) {
			if (this.types.get(i) != null) {
				ret.add(this.types.get(i).getName());
				if (ids != null) {
					ids.add(new Integer(this.types.get(i).getId()));
				}
			}
		}
		return ret;
	}

	public ArrayList<String> getPriorityLabels(final ArrayList<Integer> ids) {
		final ArrayList<String> ret = new ArrayList<String>();
		for (int i = 0; i < this.priorities.size(); i++) {
			if (this.priorities.get(i) != null) {
				ret.add(this.priorities.get(i).getName());
				if (ids != null) {
					ids.add(new Integer(this.priorities.get(i).getId()));
				}
			}
		}
		return ret;
	}

	public ArrayList<String> getStatusesLabels(final ArrayList<Integer> ids) {
		final ArrayList<String> ret = new ArrayList<String>();
		for (int i = 0; i < this.statuses.size(); i++) {
			if (this.statuses.get(i) != null) {
				ret.add(this.statuses.get(i).getName());
				if (ids != null) {
					ids.add(new Integer(this.statuses.get(i).getId()));
				}
			}
		}
		return ret;
	}

	public void doRefresh() {
		try {
			doLogin(true);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	interface OnIssueListLoadedListener {
		public void onIssueListLoaded(ArrayList<JiraIssue> issueList);
	}

	public void getIssueListAsync(final String filter, final OnIssueListLoadedListener listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				ArrayList<JiraIssue> list;
				try {
					list = loadIssues(filter);
					listener.onIssueListLoaded(list);
				} catch (final XMLRPCException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public JiraIssue getIssueForKey(final String key) {
		JiraIssue ret = null;
		if (this.issueList == null) {
			return null;
		}
		for (int i = 0; i < this.issueList.size(); i++) {
			if (this.issueList.get(i).getKey().equals(key)) {
				ret = this.issueList.get(i);
				break;
			}
		}
		return ret;
	}

	interface OnIssueLoadedListener {
		public void onIssueLoaded(JiraIssue issue);
	}

	public void getIssueAsync(final String key, final boolean loadComments, final OnIssueLoadedListener listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				JiraIssue issue;
				try {
					issue = loadIssue(key);
					if (loadComments) {
						issue.setComments(getComments(key));
					}
					listener.onIssueLoaded(issue);
				} catch (final XMLRPCException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	interface OnSearchCompleteListener {
		public void onSearchComplete(ArrayList<JiraIssue> issues);
	}

	public void searchIssueAsync(final String query, final OnSearchCompleteListener listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				ArrayList<JiraIssue> issues;
				try {
					issues = searchIssues(query);
					listener.onSearchComplete(issues);
				} catch (final XMLRPCException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void updateIssue(final String key, final Map<String, Vector> map) {
		try {
			this.rpcClient.call("jira1.updateIssue", this.loginToken, key, map);
		} catch (final XMLRPCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean hasCredentials() {
		return ((this.JIRA_URI != null) && (this.JIRA_URI.length() > 0) && (this.USER_NAME != null) && (this.USER_NAME.length() > 0) && (this.PASSWORD != null) && (this.PASSWORD
				.length() > 0));
	}

	/**
	 * This method tests if the credentials set for this connection are valid.
	 * It is used by the creation of server in order to check if the data is
	 * valid.
	 * 
	 */
	public static void testLogin(final String jiraUri, final String jiraUserName, final String jiraPass, final LoginListener loginListener) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
			@Override
			public void run() {
				if ((jiraUri != null) && (jiraUri.length() < 1)) {
					return;
				}
				if ((jiraUserName != null) && (jiraUserName.length() < 1)) {
					return;
				}
				if ((jiraPass != null) && (jiraPass.length() < 1)) {
					return;
				}
				try {
					final XMLRPCClient rpcClient = new XMLRPCClient(jiraUri + RPC_PATH);
					rpcClient.call("jira1.login", jiraUserName, jiraPass);
				} catch (final Exception e) {
					Log.e(TAG, "Error during testLogin.", e);
					if (loginListener != null) {
						loginListener.onLoginError(e);
					}
					return;
				}

				if (loginListener != null) {
					loginListener.onLoginComplete();
				}
			}
		}).start();
	}

	@Override
	public IBinder onBind(final Intent intent) {
		return new LocalBinder();
	}

	/**
	 * Use getInstance() to connect to JiraConn Service
	 * 
	 * @author josh
	 */
	public class LocalBinder extends Binder {
		public JiraConn getInstance() {
			return JiraConn.this;
		}
	}

	/**
	 * @return true if connect established
	 */
	public boolean isConnected() {
		return this.connected.get();
	}

}
