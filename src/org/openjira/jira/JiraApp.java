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

import org.openjira.jira.model.JiraServer;
import org.openjira.jiraservice.JiraContentProvider;

import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.preference.PreferenceManager;

public class JiraApp extends Application implements OnSharedPreferenceChangeListener {

	private static JiraApp app;

	public boolean allowAllSSL;

	public JiraConn conn;

	private void loadPreferences(final SharedPreferences prefs, final boolean startup) {

		this.allowAllSSL = prefs.getBoolean("allowallssl", false);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		app = this;

		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		loadPreferences(prefs, true);
		prefs.registerOnSharedPreferenceChangeListener(this);

		this.servers = getServerArrayList();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	public static JiraApp get() {
		return app;
	}

	@Override
	public void onSharedPreferenceChanged(final SharedPreferences arg0, final String arg1) {
		loadPreferences(arg0, false);
	}

	public JiraConn getCurrentConnection() {
		return this.conn;
	}

	public void setCurrentConnection(final JiraConn conn) {
		this.conn = conn;
	}

	ArrayList<JiraServer> servers = new ArrayList<JiraServer>();

	public void addServer(final String name, final String url, final String user, final String pass) {
		final ContentValues args = new ContentValues();
		args.put(JiraServersDB.KEY_NAME, name);
		args.put(JiraServersDB.KEY_URL, url);
		args.put(JiraServersDB.KEY_USER, user);
		args.put(JiraServersDB.KEY_PASSWORD, pass);

		final ContentResolver cr = getContentResolver();
		cr.insert(JiraContentProvider.CONTENT_URI, args);

		this.servers = getServerArrayList();
	}

	private ArrayList<JiraServer> getServerArrayList() {
		final ArrayList<JiraServer> jiraServerList = new ArrayList<JiraServer>();
		final ContentResolver cr = getContentResolver();
		final Cursor serverCursor = cr.query(JiraContentProvider.CONTENT_URI, null, null, null, null);
		for (int i = 0; i < serverCursor.getCount(); i++) {
			serverCursor.moveToPosition(i);
			jiraServerList.add(new JiraServer(serverCursor.getInt(0), serverCursor.getString(1), serverCursor.getString(2), serverCursor.getString(3),
					serverCursor.getString(4)));
		}
		serverCursor.close();
		return jiraServerList;
	}

	public ArrayList<JiraServer> getServerList() {
		return this.servers;
	}

	public JiraServer getServerFromName(final String name) {
		for (int i = 0; i < this.servers.size(); i++) {
			if (this.servers.get(i).getName().equals(name)) {
				return this.servers.get(i);
			}
		}
		return null;
	}

	/**
	 * delete a given server from database
	 * 
	 * @param serverName
	 *            Servername that is being deleted
	 */
	public void deleteServer(final String serverName) {
		final ContentResolver cr = getContentResolver();
		cr.delete(JiraContentProvider.CONTENT_URI, JiraServersDB.KEY_NAME + "=\"" + serverName + "\"", null);
		// db.deleteServer(getServerFromName(serverName));
		// TODO: inefficient but simple... need to change it for performance
		// issues
		this.servers = getServerArrayList();
	}

	/**
	 * Update the server using the details provided. The server to be updated is
	 * specified using the serverId.
	 * 
	 * On successful update the serverlist will be reloaded
	 * 
	 * @see org.openjira.jira.JiraServersDB
	 * 
	 * @param serverName
	 * @param password
	 * @param username
	 * @param serverUrl
	 */
	public void updateServer(final int serverId, final String serverName, final String serverUrl, final String username, final String password) {
		final ContentValues args = new ContentValues();
		args.put(JiraServersDB.KEY_NAME, serverName);
		args.put(JiraServersDB.KEY_URL, serverUrl);
		args.put(JiraServersDB.KEY_USER, username);
		args.put(JiraServersDB.KEY_PASSWORD, password);

		final ContentResolver cr = getContentResolver();
		cr.update(JiraContentProvider.CONTENT_URI, args, JiraServersDB.KEY_ID + "=" + serverId, null);
		this.servers = getServerArrayList();
	}
}
