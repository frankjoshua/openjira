package org.alexdalton.jira;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.alexdalton.jira.model.JiraComment;
import org.alexdalton.jira.model.JiraFilter;
import org.alexdalton.jira.model.JiraIssue;
import org.alexdalton.jira.model.JiraPriority;
import org.alexdalton.jira.model.JiraProject;
import org.alexdalton.jira.model.JiraResolution;
import org.alexdalton.jira.model.JiraServer;
import org.alexdalton.jira.model.JiraServerInfo;
import org.alexdalton.jira.model.JiraStatus;
import org.alexdalton.jira.model.JiraType;
import org.alexdalton.jira.model.JiraVersion;
import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;
import org.xmlrpc.android.XMLRPCFault;

import android.graphics.Bitmap;
import android.util.Log;

public class JiraConn {

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

	private ArrayList<LoginListener> loginListeners;

	public String JIRA_URI;
	public static final String RPC_PATH = "/rpc/xmlrpc";
	public String USER_NAME;
	public String PASSWORD;

	private JiraServer server;
	private JiraDB db;

	public JiraConn(JiraServer server) {
		this.server = server;
		loginListeners = new ArrayList<JiraConn.LoginListener>();
		JIRA_URI = server.getUrl();
		USER_NAME = server.getUser();
		PASSWORD = server.getPassword();
		if (server != null) {
			db = new JiraDB(JiraApp.get(), server.getName());
		}
	}

	public JiraConn(String server, String user, String password) {
		JIRA_URI = server;
		USER_NAME = user;
		PASSWORD = password;
	}

	public Object getLoginToken() {
		return loginToken;
	}

	public void loadFilters() throws Exception {
		Object[] f;
		try {
			f = (Object[]) rpcClient.call("jira1.getFavouriteFilters",
					loginToken);
		} catch (final XMLRPCFault e) {
			// added compatibility for JIRA < 4.x; for more informations see
			// https://studio.plugins.atlassian.com/wiki/display/JRPC/JIRA+RPC+Plugin
			f = (Object[]) rpcClient.call("jira1.getSavedFilters", loginToken);
		}
		filters.clear();
		for (int i = 0; i < f.length; i++) {
			JiraFilter filter = JiraFilter.fromMap((Map) f[i]);
			filters.add(filter);
			db.updateFilter(filter);
			reportSyncProgress(f, i);
		}
	}

	protected void reportSyncProgress(final String message, final int f, int i) {
		for (LoginListener loginListener : loginListeners) {
			loginListener.onSyncProgress(message, i, f);
		}
	}
	
	protected void reportSyncProgress(Object[] f, int i) {
		reportSyncProgress(null, f.length, i);
	}

	protected void reportLoginComplete() {
		for (LoginListener loginListener : loginListeners) {
			loginListener.onLoginComplete();
		}
		loginListeners.clear();
	}

	protected void reportLoginError(final Exception e) {
		for (LoginListener loginListener : loginListeners) {
			loginListener.onLoginError(e);
		}
		loginListeners.clear();
	}

	public ArrayList<JiraFilter> getFilters() {
		return filters;
	}

	public void loadResolutions() throws Exception {
		Object[] f = (Object[]) rpcClient.call("jira1.getResolutions",
				loginToken);
		for (int i = 0; i < f.length; i++) {
			JiraResolution j = JiraResolution.fromMap((Map) f[i]);
			resolutions.put(j.getId(), j);
			db.updateResolution(j);
			reportSyncProgress(f, i);
		}
	}

	public void loadStatuses() throws Exception {
		Object[] f = (Object[]) rpcClient.call("jira1.getStatuses", loginToken);
		for (int i = 0; i < f.length; i++) {
			Map status = (Map) f[i];
			JiraStatus j = JiraStatus.fromMap(status, JIRA_URI);
			statuses.put(j.getId(), j);
			db.updateStatus(j);
			reportSyncProgress(f, i);
		}
	}

	public String getStatusLabel(int status) {
		JiraStatus s = statuses.get(status);

		return s == null ? null : s.getName();
	}

	public String getStatusIcon(int status) {
		JiraStatus s = statuses.get(status);
		return s == null ? null : s.getIcon();
	}

	public Bitmap getStatusBitmap(int status) {
		JiraStatus s = statuses.get(status);
		return s == null ? null : s.getBitmap();
	}

	public void loadPriorities() throws Exception {
		Object[] f = (Object[]) rpcClient.call("jira1.getPriorities",
				loginToken);
		for (int i = 0; i < f.length; i++) {
			Map prio = (Map) f[i];
			JiraPriority j = JiraPriority.fromMap(prio, JIRA_URI);
			priorities.put(j.getId(), j);
			db.updatePriority(j);
			reportSyncProgress(f, i);
		}
	}

	public String getPriorityLabel(int prio) {
		return priorities.get(prio) == null ? "Unknown" : priorities.get(prio)
				.getName();
	}

	public String getPriorityIcon(int prio) {
		return priorities.get(prio) == null ? "" : priorities.get(prio)
				.getIcon();
	}

	public Bitmap getPriorityBitmap(int prio) {
		return priorities.get(prio).getBitmap();
	}

	public void loadTypes() throws Exception {
		Object[] f = (Object[]) rpcClient.call("jira1.getIssueTypes",
				loginToken);
		for (int i = 0; i < f.length; i++) {
			Map prio = (Map) f[i];
			JiraType j = JiraType.fromMap(prio, JIRA_URI);
			types.put(j.getId(), j);
			db.updateType(j);
			reportSyncProgress(f, i);
		}
	}

	public String getTypeLabel(int prio) {
		return types.get(prio) == null ? "Unknown" : types.get(prio).getName();
	}

	public String getTypeIcon(int prio) {
		return types.get(prio) == null ? "" : types.get(prio).getIcon();
	}

	public Bitmap getTypeBitmap(int prio) {
		return types.get(prio).getBitmap();
	}

	public void loadProjects() throws Exception {
		Object[] projects;
		try {
			projects = (Object[]) rpcClient.call("jira1.getProjectsNoSchemes",
					loginToken);
		} catch (final XMLRPCFault e) {
			// added compatibility for JIRA < 3.13; for more informations see
			// https://studio.plugins.atlassian.com/wiki/display/JRPC/JIRA+RPC+Plugin
			projects = (Object[]) rpcClient.call("jira1.getProjects",
					loginToken);
		}

		// Print projects
		for (int i = 0; i < projects.length; i++) {
			reportSyncProgress(projects, i);
			Map project = (Map) projects[i];
			JiraProject p = JiraProject.fromMap(project);
			projectList.add(p);
			ArrayList<JiraVersion> versions = getVersions(p.getTag());
			p.setVersions(versions);
			db.updateProject(p);
		}
	}

	public ArrayList<JiraProject> getProjects() {
		return projectList;
	}

	public interface LoginListener {
		public void onLoginComplete();

		public void onLoginError(Exception e);

		public void onSyncProgress(String message, int progress, int max);
	}

	public void setOnLoginListener(final LoginListener listener) {
		loginListeners.add(listener);
	}

	public void doLogin(final boolean force) throws Exception {
		new Thread(new Runnable() {
			public void run() {
				if (JIRA_URI != null && JIRA_URI.length() < 1) {
					return;
				}
				if (USER_NAME != null && USER_NAME.length() < 1) {
					return;
				}
				if (PASSWORD != null && PASSWORD.length() < 1) {
					return;
				}
				try {
					rpcClient = new XMLRPCClient(JIRA_URI + RPC_PATH);
				} catch (final Exception e) {
					e.printStackTrace();
					reportLoginError(e);
					return;
				}
				try {
					loginToken = rpcClient.call("jira1.login", USER_NAME,
							PASSWORD);
				} catch (final Exception e) {
					e.printStackTrace();
					reportLoginError(e);
				}

				filters = db.getFilters();

				if (filters != null && filters.size() > 0 && !force) {
					projectList = db.getProjects();
					statuses = db.getStatuses();
					priorities = db.getPriorities();
					// resolutions = db.getResolutions();
					types = db.getTypes();
					reportLoginComplete();
					db.close();
				} else {
					try {
						reportSyncProgress("Syncing Filters", 0, 1);
						loadFilters();
						reportSyncProgress("Syncing Projects", 0, 1);
						loadProjects();
						reportSyncProgress("Syncing Statuses", 0, 1);
						loadStatuses();
						reportSyncProgress("Syncing Priorities", 0, 1);
						loadPriorities();
						reportSyncProgress("Syncing Resolutions", 0, 1);
						loadResolutions();
						reportSyncProgress("Syncing Types", 0, 1);
						loadTypes();
						serverInfo = getServerInfo();
						reportLoginComplete();
					} catch (final Exception e) {
						e.printStackTrace();
						reportLoginError(e);
					}
				}
			}

		}).start();
	}

	public void createIssue(String project, String priority, String type,
			String assignee, String reporter, String summary, String description)
			throws XMLRPCException {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("summary", summary);
		params.put("project", project);
		params.put("type", type);
		params.put("priority", priority);
		params.put("description", description);
		params.put("assignee", USER_NAME);
		params.put("reporter", USER_NAME);

		HashMap<String, Object> issue = (HashMap<String, Object>) rpcClient
				.call("jira1.createIssue", loginToken, params);
		// Toast.makeText(context, "issue created", Toast.LENGTH_SHORT).show();
		Log.v("test", "issue creation complete...");
	}

	public ArrayList<JiraIssue> loadIssues(String filter)
			throws XMLRPCException {
		Object[] issues = (Object[]) rpcClient.call(
				"jira1.getIssuesFromFilter", loginToken, filter);
		issueList = new ArrayList<JiraIssue>();
		for (int i = 0; i < issues.length; i++) {
			Map issue = (Map) issues[i];
			issueList.add(JiraIssue.fromMap(issue));
		}
		return issueList;
	}

	public ArrayList<JiraIssue> searchIssues(String query)
			throws XMLRPCException {
		Object[] issues = (Object[]) rpcClient.call(
				"jira1.getIssuesFromTextSearch", loginToken, query);
		issueList = new ArrayList<JiraIssue>();
		for (int i = 0; i < issues.length; i++) {
			Map issue = (Map) issues[i];
			issueList.add(JiraIssue.fromMap(issue));
		}
		return issueList;
	}

	public JiraIssue loadIssue(String key) throws XMLRPCException {
		Map issue = (Map) rpcClient.call("jira1.getIssue", loginToken, key);
		return JiraIssue.fromMap(issue);
	}

	public JiraProject getProject(String projectId) {
		for (int i = 0; i < projectList.size(); i++) {
			if (projectList.get(i).getTag().equals(projectId))
				return projectList.get(i);
		}
		return null;
	}

	public ArrayList<JiraComment> getComments(String key)
			throws XMLRPCException {
		Object[] comments = (Object[]) rpcClient.call("jira1.getComments",
				loginToken, key);
		if (comments.length > 0) {
			ArrayList<JiraComment> ret = new ArrayList<JiraComment>();
			for (int i = 0; i < comments.length; i++) {
				Map comment = (Map) comments[i];
				ret.add(JiraComment.fromMap(comment));
			}
			return ret;
		} else
			return null;
	}

	public ArrayList<JiraVersion> getVersions(String projectKey)
			throws XMLRPCException {
		Object[] versions = (Object[]) rpcClient.call("jira1.getVersions",
				loginToken, projectKey);
		if (versions.length > 0) {
			ArrayList<JiraVersion> ret = new ArrayList<JiraVersion>();
			for (int i = 0; i < versions.length; i++) {
				HashMap version = (HashMap) versions[i];
				ret.add(JiraVersion.fromMap(version));
			}
			return ret;
		} else
			return null;
	}

	public void addComment(String key, String comment) throws XMLRPCException {
		rpcClient.call("jira1.addComment", loginToken, key, comment);
	}

	public interface OnCommentAddedListener {
		public void onCommentAdded(boolean success);
	}

	public void addCommentAsync(final String key, final String comment,
			final OnCommentAddedListener listener) {
		new Thread(new Runnable() {
			public void run() {
				try {
					addComment(key, comment);
				} catch (XMLRPCException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					listener.onCommentAdded(false);
					return;
				}
				listener.onCommentAdded(true);
			}
		}).start();
	}

	public ArrayList<JiraComment> getUser(String search) throws XMLRPCException {
		Map user = (Map) rpcClient.call("jira1.getUser", loginToken, search);
		Log.v("Test", "got User: " + user);
		return null;
	}

	public JiraServerInfo getServerInfo() throws XMLRPCException {
		Map map = (Map) rpcClient.call("jira1.getServerInfo", loginToken);

		JiraServerInfo info = JiraServerInfo.fromMap(map);
		Log.v("test", "Server info : " + map);
		return info;
	}

	public ArrayList<JiraIssue> getIssueList() {
		return issueList;
	}

	public ArrayList<String> getProjectsLabels(ArrayList<String> ids) {
		ArrayList<String> ret = new ArrayList<String>();
		for (int i = 0; i < projectList.size(); i++) {
			if (projectList.get(i) != null) {
				ret.add(projectList.get(i).getName());
				if (ids != null) {
					ids.add(projectList.get(i).getTag());
				}
			}
		}
		return ret;
	}

	public ArrayList<String> getTypeLabels(ArrayList<Integer> ids) {
		ArrayList<String> ret = new ArrayList<String>();
		for (int i = 0; i < types.size(); i++) {
			if (types.get(i) != null) {
				ret.add(types.get(i).getName());
				if (ids != null) {
					ids.add(new Integer(types.get(i).getId()));
				}
			}
		}
		return ret;
	}

	public ArrayList<String> getPriorityLabels(ArrayList<Integer> ids) {
		ArrayList<String> ret = new ArrayList<String>();
		for (int i = 0; i < priorities.size(); i++) {
			if (priorities.get(i) != null) {
				ret.add(priorities.get(i).getName());
				if (ids != null) {
					ids.add(new Integer(priorities.get(i).getId()));
				}
			}
		}
		return ret;
	}

	public ArrayList<String> getStatusesLabels(ArrayList<Integer> ids) {
		ArrayList<String> ret = new ArrayList<String>();
		for (int i = 0; i < statuses.size(); i++) {
			if (statuses.get(i) != null) {
				ret.add(statuses.get(i).getName());
				if (ids != null) {
					ids.add(new Integer(statuses.get(i).getId()));
				}
			}
		}
		return ret;
	}

	public void doRefresh() {
		try {
			doLogin(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	interface OnIssueListLoadedListener {
		public void onIssueListLoaded(ArrayList<JiraIssue> issueList);
	}

	public void getIssueListAsync(final String filter,
			final OnIssueListLoadedListener listener) {
		new Thread(new Runnable() {
			public void run() {
				ArrayList<JiraIssue> list;
				try {
					list = loadIssues(filter);
					listener.onIssueListLoaded(list);
				} catch (XMLRPCException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public JiraIssue getIssueForKey(String key) {
		JiraIssue ret = null;
		if (issueList == null)
			return null;
		for (int i = 0; i < issueList.size(); i++) {
			if (issueList.get(i).getKey().equals(key)) {
				ret = issueList.get(i);
				break;
			}
		}
		return ret;
	}

	interface OnIssueLoadedListener {
		public void onIssueLoaded(JiraIssue issue);
	}

	public void getIssueAsync(final String key, final boolean loadComments,
			final OnIssueLoadedListener listener) {
		new Thread(new Runnable() {
			public void run() {
				JiraIssue issue;
				try {
					issue = loadIssue(key);
					if (loadComments)
						issue.setComments(getComments(key));
					listener.onIssueLoaded(issue);
				} catch (XMLRPCException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	interface OnSearchCompleteListener {
		public void onSearchComplete(ArrayList<JiraIssue> issues);
	}

	public void searchIssueAsync(final String query,
			final OnSearchCompleteListener listener) {
		new Thread(new Runnable() {
			public void run() {
				ArrayList<JiraIssue> issues;
				try {
					issues = searchIssues(query);
					listener.onSearchComplete(issues);
				} catch (XMLRPCException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void updateIssue(String key, Map<String, Vector> map) {
		try {
			rpcClient.call("jira1.updateIssue", loginToken, key, map);
		} catch (XMLRPCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean hasCredentials() {
		return (JIRA_URI != null && JIRA_URI.length() > 0 && USER_NAME != null
				&& USER_NAME.length() > 0 && PASSWORD != null && PASSWORD
				.length() > 0);
	}

	/**
	 * This method tests if the credentials set for this connection are valid.
	 * It is used by the creation of server in order to check if the data is
	 * valid.
	 * 
	 */
	public void testLogin() {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
			public void run() {
				if (JIRA_URI != null && JIRA_URI.length() < 1) {
					return;
				}
				if (USER_NAME != null && USER_NAME.length() < 1) {
					return;
				}
				if (PASSWORD != null && PASSWORD.length() < 1) {
					return;
				}
				try {
					rpcClient = new XMLRPCClient(JIRA_URI + RPC_PATH);
				} catch (final Exception e) {
					e.printStackTrace();
					reportLoginError(e);
					return;
				}
				try {
					loginToken = rpcClient.call("jira1.login", USER_NAME,
							PASSWORD);
				} catch (final Exception e) {
					e.printStackTrace();
					reportLoginError(e);
					return;
				}
				reportLoginComplete();
			}
		}).start();
	}

}
