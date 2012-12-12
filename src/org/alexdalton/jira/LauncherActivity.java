package org.alexdalton.jira;

import java.util.ArrayList;

import org.alexdalton.jira.JiraConn.LoginListener;
import org.alexdalton.jira.model.JiraFilter;

import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LauncherActivity extends BaseActivity implements OnClickListener,
		LoginListener {

	private final static Handler HANDLER = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.dashboard);
		findViewById(R.id.btnServerList).setOnClickListener(this);
		findViewById(R.id.btnFilters).setOnClickListener(this);
		findViewById(R.id.btnIssue).setOnClickListener(this);

	}

	@Override
	protected void onResume() {
		super.onResume();
		final JiraConn conn = JiraApp.get().conn;
		if (conn != null) {
			final ArrayList<JiraFilter> filters = conn.getFilters();
			if (filters != null && filters.size() > 0) {
				setExtraButton();
			} else {
				conn.setOnLoginListener(this);
			}
		}
	}

	private void setExtraButton() {
		HANDLER.post(new Runnable() {

			@Override
			public void run() {
				final Button btnExtra = (Button) findViewById(R.id.btnExtra);
				btnExtra.setOnClickListener(LauncherActivity.this);
				final int lastFilterId = PreferenceManager
						.getDefaultSharedPreferences(LauncherActivity.this)
						.getInt(JiraApp.LAST_FILTER, 0);
				final String lastFilterName = JiraApp.get().conn.getFilters()
						.get(lastFilterId).getName();
				btnExtra.setText(lastFilterName);
			}
		});
	}

	@Override
	public void onClick(View v) {
		//If no active connection only allow access to server list
        final int id = v.getId();
		if(id != R.id.btnServerList && isActiveConnection() == false){
        	noServerFoundToast();
        	return;
        }
		switch (id) {
		case R.id.btnServerList:
			launchServerList();
			break;
		case R.id.btnFilters:
			launchFilters();
			break;
		case R.id.btnIssue:
			launchCreateIssue();
			break;
		case R.id.btnExtra:
			launchLastFilter();
			return;
		default:
			return;
		}

	}
	
	@Override
	public void onLoginComplete() {
		setExtraButton();
	}

	@Override
	public void onLoginError(Exception e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSyncProgress(String message, int progress, int max) {
		// TODO Auto-generated method stub

	}

}
