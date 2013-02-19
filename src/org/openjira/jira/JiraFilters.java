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

import org.openjira.jira.JiraConn.LocalBinder;
import org.openjira.jira.JiraConn.LoginListener;
import org.openjira.jira.model.JiraFilter;
import org.openjira.jira.model.JiraServer;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class JiraFilters extends OJActivity implements OnItemClickListener, LoginListener {

	JiraConn conn;

	private final ServiceConnection connection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(final ComponentName name) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onServiceConnected(final ComponentName name, final IBinder service) {
			final JiraConn jiraConn = ((LocalBinder) service).getInstance();
			JiraFilters.this.conn = jiraConn;

			final Uri data = getIntent().getData();

			if (data != null) {
				final JiraApp app = JiraApp.get();
				final JiraServer server = app.getServerFromName(data.getQueryParameter("server"));
				// this.conn = new JiraConn(server);
				// app.setCurrentConnection(JiraFilters.this.conn);
				if (jiraConn != null) {
					jiraConn.setOnLoginListener(JiraFilters.this);

					if (jiraConn.isConnected() == false) {
						JiraFilters.this.progressDialog = new ProgressDialog(JiraFilters.this);
						JiraFilters.this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
						JiraFilters.this.progressDialog.setMax(100);
						JiraFilters.this.progressDialog.setTitle("Syncing");
						JiraFilters.this.progressDialog.show();
						findViewById(R.id.progress).setVisibility(View.VISIBLE);
						try {
							jiraConn.setServer(server);
						} catch (final Exception e) {
							e.printStackTrace();
							findViewById(R.id.progress).setVisibility(View.GONE);
						}
					}
				} else {
					Toast.makeText(JiraFilters.this, "Go to preferences to setup your account details", Toast.LENGTH_LONG).show();
				}
			}
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filters);
		setTheme(android.R.style.Theme_Light);

		findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				final EditText et = (EditText) findViewById(R.id.query);
				final String query = et.getText().toString();
				if ((query != null) && (query.length() > 0)) {
					final Intent i = new Intent(JiraFilters.this, Search.class);
					i.setData(Uri.parse("jira://search?query=" + query));
					startActivity(i);
				}
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		final Intent service = new Intent(this, JiraConn.class);
		bindService(service, this.connection, Service.BIND_AUTO_CREATE);
	}

	@Override
	public void onStop() {
		super.onStop();
		try {
			unbindService(this.connection);
		} catch (final Exception e) {
			// Service was not connected
		}
	}

	// private int currentIssue;
	// private int currentFilter;

	// XMLRPCClient rpcClient;
	// Object loginToken;
	// ArrayList<JiraIssue> issueList;

	public void getFavouriteFilters() {
		final ArrayList<String> filterNames = new ArrayList<String>();
		final ArrayList<JiraFilter> filters = this.conn.getFilters();
		for (int i = 0; i < filters.size(); i++) {
			filterNames.add(filters.get(i).getName());
		}
		final ListView list = (ListView) findViewById(R.id.filterList);
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.listitem, R.id.text, filterNames);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
	}

	// public void logout() throws XMLRPCException {
	// Boolean bool = (Boolean) rpcClient.call("jira1.logout", loginToken);
	// Log.i("", "Logout successful: " + bool);
	// }

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		menu.add(0, MENU_PREFERENCES, 0, R.string.preferences).setIcon(android.R.drawable.ic_menu_preferences);
		menu.add(0, MENU_NEW, 1, "New").setIcon(android.R.drawable.ic_menu_add);
		menu.add(0, MENU_REFRESH, 2, "Refresh").setIcon(android.R.drawable.ic_menu_upload);
		menu.add(0, MENU_SEARCH, 3, "Search").setIcon(android.R.drawable.ic_menu_search);
		menu.add(0, MENU_ABOUT, 4, "About").setIcon(android.R.drawable.ic_menu_info_details);

		return super.onCreateOptionsMenu(menu);
	}

	private static final int MENU_PREFERENCES = 100;
	private static final int MENU_NEW = 101;
	private static final int MENU_REFRESH = 102;
	private static final int MENU_SEARCH = 103;
	private static final int MENU_ABOUT = 104;

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		boolean handled = false;
		switch (item.getItemId()) {
		case MENU_REFRESH:
			this.progressDialog = new ProgressDialog(this);
			this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			this.progressDialog.setMax(100);
			this.progressDialog.setTitle("Syncing");
			this.progressDialog.show();
			this.conn.doRefresh();
			handled = true;
			break;
		case MENU_PREFERENCES:
			startActivity(new Intent(this, JiraPreferences.class));
			handled = true;
			break;
		case MENU_NEW:
			startActivity(new Intent(this, CreateIssue.class));
			handled = true;
			break;
		case MENU_SEARCH:
			startActivity(new Intent(this, Search.class));
			handled = true;
			break;
		case MENU_ABOUT:
			startActivity(new Intent(this, About.class));
			handled = true;
			break;
		}
		return handled;
	}

	public void projectSelected() {

	}

	@Override
	public void onItemClick(final AdapterView<?> arg0, final View arg1, final int arg2, final long arg3) {
		try {
			if (arg0.equals(findViewById(R.id.filterList))) {
				final Intent i = new Intent(this, IssueList.class);
				i.putExtra("filter", this.conn.getFilters().get(arg2).getId());
				i.putExtra("filterName", this.conn.getFilters().get(arg2).getName());
				startActivity(i);
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onLoginComplete() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				findViewById(R.id.progress).setVisibility(View.VISIBLE);
				getFavouriteFilters();
				findViewById(R.id.progress).setVisibility(View.GONE);
				if ((JiraFilters.this.progressDialog != null) && JiraFilters.this.progressDialog.isShowing()) {
					JiraFilters.this.progressDialog.dismiss();
				}
			}
		});
	}

	@Override
	public void onLoginError(final Exception e) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(JiraFilters.this, "Connection error: " + e, Toast.LENGTH_SHORT).show();
			}
		});
	}

	ProgressDialog progressDialog;

	@Override
	public void onSyncProgress(final String message, final int progress, final int max) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if ((JiraFilters.this.progressDialog != null) && JiraFilters.this.progressDialog.isShowing()) {
					if (message != null) {
						JiraFilters.this.progressDialog.setTitle(message);
					}
					JiraFilters.this.progressDialog.setMax(max);
					JiraFilters.this.progressDialog.setProgress(progress);
				}
			}
		});
	}

}
