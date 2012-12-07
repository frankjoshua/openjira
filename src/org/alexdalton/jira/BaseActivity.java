package org.alexdalton.jira;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class BaseActivity extends Activity {

	private ProgressDialog progressDialog;

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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
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
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setMax(100);
		progressDialog.setTitle("Syncing");
		progressDialog.show();
		findViewById(R.id.progress).setVisibility(View.VISIBLE);
		try {
			//TODO: Login
		    conn.doLogin(false);
		} catch (Exception e) {
		    e.printStackTrace();
		    findViewById(R.id.progress).setVisibility(View.GONE);
		}
	}
}
