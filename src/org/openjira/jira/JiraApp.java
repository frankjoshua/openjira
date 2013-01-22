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
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.preference.PreferenceManager;

public class JiraApp extends Application implements
	OnSharedPreferenceChangeListener {

    private static JiraApp app;

    public boolean allowAllSSL;

    public JiraConn conn;

    private JiraServersDB db;

    private void loadPreferences(SharedPreferences prefs, boolean startup) {

	allowAllSSL = prefs.getBoolean("allowallssl", false);
    }

    @Override
    public void onCreate() {
	super.onCreate();
	app = this;

	SharedPreferences prefs = PreferenceManager
		.getDefaultSharedPreferences(this);
	loadPreferences(prefs, true);
	prefs.registerOnSharedPreferenceChangeListener(this);
	db = new JiraServersDB(this);
	servers = getServerArrayList();
    }

    @Override
    public void onTerminate() {
	super.onTerminate();
    }

    public static JiraApp get() {
	return app;
    }

    public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
	loadPreferences(arg0, false);
    }

    public JiraConn getCurrentConnection() {
	return conn;
    }

    public void setCurrentConnection(JiraConn conn) {
	this.conn = conn;
    }

    ArrayList<JiraServer> servers = new ArrayList<JiraServer>();

    public void addServer(String name, String url, String user, String pass) {
	// TODO Auto-generated method stub
	db.addServer(new JiraServer(name, url, user, pass));
	servers = getServerArrayList();
    }

	private ArrayList<JiraServer> getServerArrayList() {
		final ArrayList<JiraServer> jiraServerList = new ArrayList<JiraServer>();
		ContentResolver cr = getContentResolver();
		Cursor serverCursor = cr.query(JiraContentProvider.CONTENT_URI, null, null, null, null);
		 for (int i = 0; i < serverCursor.getCount(); i++) {
			 serverCursor.moveToPosition(i);
			 jiraServerList.add(new JiraServer(serverCursor.getInt(0), serverCursor.getString(1), serverCursor.getString(2), serverCursor.getString(3), serverCursor.getString(4)));
	        }
		 serverCursor.close();
		return jiraServerList;
	}

    public ArrayList<JiraServer> getServerList() {
	return servers;
    }

    public JiraServer getServerFromName(String name) {
	for (int i = 0; i < servers.size(); i++) {
	    if (servers.get(i).getName().equals(name)) {
		return servers.get(i);
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
    public void deleteServer(String serverName) {
	db.deleteServer(getServerFromName(serverName));
	// TODO: inefficient but simple... need to change it for performance
	// issues
	servers = getServerArrayList();
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
    public void updateServer(int serverId, String serverName, String serverUrl,
	    String username, String password) {
	if (db.updateServer(new JiraServer(serverId, serverName, serverUrl, username, password))) {
	    // TODO: inefficient but simple... need to change it for performance
	    // issues, reload updated server only
	    servers = getServerArrayList();
	}
    }
}
