package org.alexdalton.jira;


import org.alexdalton.jira.model.JiraServer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class BaseActivity extends Activity {
	
	// ****************** Activity return values ****************** //
	static final int ACTIVITY_ADD = 0;
	static final int ACTIVITY_EDIT = 1;
	
	private ProgressDialog progressDialog;
	static Handler HANDLER = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final JiraApp app = JiraApp.get();
		final JiraConn conn;
	
        JiraServer server = app.getServerFromName(PreferenceManager.getDefaultSharedPreferences(this).getString(JiraApp.SELECTED_SERVER, ""));
        if (server != null) {
			conn = new JiraConn(server);
			app.setCurrentConnection(conn);
		} else {
			conn = null;
		}
		if (conn != null) {
            conn.setOnLoginListener(new BaseActivityLoginListener(this, progressDialog));

            if (conn.hasCredentials()) {
                showProgressDialog(conn);
            }
        }
	}

	final protected void launchCreateIssue() {
		final Intent intent = new Intent(this, CreateIssue.class);
		startActivity(intent);
	}

	final protected void launchFilters() {

		final Intent intent = new Intent(this, JiraFilters.class);
		intent.setData(Uri.parse("jira://showFilters?server="
				+ JiraApp.get().servers.get(0).getName()));
		startActivity(intent);

	}

	final protected void launchServerList() {
		final Intent intent = new Intent(this, ServerList.class);
		startActivity(intent);
	}

	final protected void launchLastFilter() {

		Intent i = new Intent(this, IssueList.class);
		final int lastFilterId = PreferenceManager.getDefaultSharedPreferences(
				this).getInt(JiraApp.LAST_FILTER, 0);
		i.putExtra("filter", JiraApp.get().conn.getFilters().get(lastFilterId)
				.getId());
		i.putExtra("filterName",
				JiraApp.get().conn.getFilters().get(lastFilterId).getName());
		startActivity(i);

	}

	/**
	 * Check for active connection
	 * 
	 * @return true if available
	 */
	protected boolean isActiveConnection() {
		// TODO: Need a more reliable method
		final JiraConn currentConnection = JiraApp.get().getCurrentConnection();
		if (currentConnection != null && currentConnection.hasCredentials()) {
			return true;
		}
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//If no active connection only allow access to server list
		final int id = item.getItemId();
		if(id != R.id.menu_server_list && isActiveConnection() == false){
        	noServerFoundToast();
        	addServer();
        	return super.onOptionsItemSelected(item);
        }
		switch (id) {
		case R.id.menu_last_filter:
			launchLastFilter();
			return true;
		case R.id.menu_new_issue:
			launchCreateIssue();
			return true;
		case R.id.menu_filters:
			launchFilters();
			return true;
		case R.id.menu_server_list:
			launchServerList();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.xml.launcher_menu, menu);
		return true;
	}

	protected void showProgressDialog(JiraConn conn) {
		setProgressDialog(new ProgressDialog(this));
		getProgressDialog().setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		getProgressDialog().setMax(100);
		getProgressDialog().setTitle("Syncing");
		getProgressDialog().show();
		//findViewById(R.id.progress).setVisibility(View.VISIBLE);
		try {
			conn.setOnLoginListener(new BaseActivityLoginListener(BaseActivity.this, getProgressDialog()));
			conn.doLogin(false);
		} catch (Exception e) {
			e.printStackTrace();
			//findViewById(R.id.progress).setVisibility(View.GONE);
		}
	}

	protected void noServerFoundToast() {
		Toast.makeText(this, "No connection found, please check server settings or add a server.",  Toast.LENGTH_LONG).show();
	}

	protected final void updateServer(int requestCode, Intent intent) {
		int serverId = 0;
		String serverName = null;
		String serverUrl = "";
		String username = "";
		String password = "";
	
		// get data from bundle
		Bundle extras = intent.getExtras();
		if (extras != null) {
			serverId = extras.getInt(JiraServersDB.KEY_ID);
			serverName = extras.getString(JiraServersDB.KEY_NAME);
			serverUrl = extras.getString(JiraServersDB.KEY_URL);
			username = extras.getString(JiraServersDB.KEY_USER);
			password = extras.getString(JiraServersDB.KEY_PASSWORD);
		}
	
		switch (requestCode) {
		case ACTIVITY_ADD:
			if (serverName != null) {
				JiraApp.get().addServer(serverName, serverUrl, username,
						password);
			}
			break;
		case ACTIVITY_EDIT:
			if (serverName != null) {
				JiraApp.get().updateServer(serverId, serverName, serverUrl,
						username, password);
			}
			break;
		}
	}

	protected final void addServer() {
		Intent i = new Intent(this, EditServer.class);
		startActivityForResult(i, ACTIVITY_ADD);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		updateServer(requestCode, data);
	}

	public ProgressDialog getProgressDialog() {
		return progressDialog;
	}

	public void setProgressDialog(ProgressDialog progressDialog) {
		this.progressDialog = progressDialog;
	}
	
	
}
