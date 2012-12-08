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
package org.alexdalton.jira;

import org.alexdalton.jira.JiraConn.LoginListener;

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
		if (JiraApp.get().conn.getFilters().size() > 0) {
			setExtraButton();
		} else {
			JiraApp.get().conn.setOnLoginListener(this);
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

		switch (v.getId()) {
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
