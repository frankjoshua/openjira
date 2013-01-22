package org.alexdalton.jira;

import java.util.ArrayList;

import org.alexdalton.jira.model.JiraServer;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;

public class JiraApp extends Application implements
		OnSharedPreferenceChangeListener {

	public static final String LAST_FILTER = "last_filter";

	public static final String SELECTED_SERVER = "selected_server";

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
		servers = db.getServerList();
		if(servers.size() > 0){
			//Set first server as default
			conn = new JiraConn(servers.get(0));
			try {
				conn.doLogin(true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
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
		servers = db.getServerList();
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
		servers = db.getServerList();
	}

	/**
	 * Update the server using the details provided. The server to be updated is
	 * specified using the serverId.
	 * 
	 * On successful update the serverlist will be reloaded
	 * 
	 * @see org.alexdalton.jira.JiraServersDB
	 * 
	 * @param serverName
	 * @param password
	 * @param username
	 * @param serverUrl
	 */
	public void updateServer(int serverId, String serverName, String serverUrl,
			String username, String password) {
		if (db.updateServer(new JiraServer(serverId, serverName, serverUrl,
				username, password))) {
			// TODO: inefficient but simple... need to change it for performance
			// issues, reload updated server only
			servers = db.getServerList();
		}
	}
}
