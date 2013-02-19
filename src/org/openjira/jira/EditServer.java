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

import org.openjira.jira.JiraConn.LoginListener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Activity to add a new or edit a JIRA-Server
 * 
 * @author Mike Sz.
 * 
 */
public class EditServer extends Activity {
	// ****************** Class Members ****************** //
	private int mServerId;
	private EditText mServernameText;
	private EditText mPasswordText;
	private EditText mUsernameText;
	private EditText mServerUrlText;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editserverlist);

		// set default intent return for hardware buttons
		final Intent mIntent = new Intent();
		setResult(RESULT_CANCELED, mIntent);

		// Get view references
		this.mServernameText = (EditText) findViewById(R.id.servername);
		this.mServerUrlText = (EditText) findViewById(R.id.serverurl);
		this.mUsernameText = (EditText) findViewById(R.id.username);
		this.mPasswordText = (EditText) findViewById(R.id.password);
		final Button submitButton = (Button) findViewById(R.id.submit);

		// Initialize values from Bundle (if it is present)
		final Bundle extras = getIntent().getExtras();
		if (extras != null) {
			this.mServerId = extras.getInt(JiraServersDB.KEY_ID);
			final String name = extras.getString(JiraServersDB.KEY_NAME);
			final String url = extras.getString(JiraServersDB.KEY_URL);
			final String username = extras.getString(JiraServersDB.KEY_USER);
			final String password = extras.getString(JiraServersDB.KEY_PASSWORD);

			if (name != null) {
				this.mServernameText.setText(name);
			}
			if (url != null) {
				this.mServerUrlText.setText(url);
			}
			if (username != null) {
				this.mUsernameText.setText(username);
			}
			if (password != null) {
				this.mPasswordText.setText(password);
			}
		}

		// Add button listener
		submitButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View view) {
				// Get input values
				final String serverName = EditServer.this.mServernameText.getText().toString();
				final String serverUrl = EditServer.this.mServerUrlText.getText().toString();
				final String username = EditServer.this.mUsernameText.getText().toString();
				final String password = EditServer.this.mPasswordText.getText().toString();

				// TODO: Validate required fields
				// TODO: need some URL validation...

				// validate connection, on failure cancel next steps
				// TODO: needs to be redesigned because connection test is
				// asynchronous and we need to wait here for the real result...
				if (testConnection(serverName, serverUrl, username, password)) {
					// Build a return bundle
				}
			}

			/**
			 * This function is testing a connection with the given URL and user
			 * credentials. For better usability it shows a ProgressDialog until
			 * the validation is on progress. (Dialog disabled currently!)
			 * 
			 * @param name
			 *            The name of the server connection
			 * @param url
			 *            complete server url e.g. http://jira.atlassian.com
			 * @param user
			 *            (optional) username for login attempts
			 * @param pass
			 *            (optional) password for login attempts
			 * @return return true on success, false otherwise
			 */
			private boolean testConnection(final String name, final String url, final String user, final String pass) {
				// final JiraConn conn = new JiraConn(url, user, pass);

				final ProgressDialog dlg = new ProgressDialog(EditServer.this);
				dlg.setMessage("Testing connection");
				dlg.show();

				JiraConn.testLogin(url, user, pass, new LoginListener() {
					@Override
					public void onLoginError(final Exception e) {
						EditServer.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								dlg.dismiss();
								Toast.makeText(EditServer.this, "Error connecting server " + e, Toast.LENGTH_LONG).show();
							}
						});
					}

					@Override
					public void onLoginComplete() {
						EditServer.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								dlg.dismiss();
								Toast.makeText(EditServer.this, "Connection successfull ", Toast.LENGTH_LONG).show();
								final Bundle bundle = new Bundle();
								bundle.putInt(JiraServersDB.KEY_ID, EditServer.this.mServerId);
								bundle.putString(JiraServersDB.KEY_NAME, name);
								bundle.putString(JiraServersDB.KEY_URL, url);
								bundle.putString(JiraServersDB.KEY_USER, user);
								bundle.putString(JiraServersDB.KEY_PASSWORD, pass);

								// Return Bundle and close
								final Intent mIntent = new Intent();
								mIntent.putExtras(bundle);
								setResult(RESULT_OK, mIntent);
								finish();

							}
						});
					}

					@Override
					public void onSyncProgress(final String message, final int progress, final int max) {
						// TODO Auto-generated method stub

					}
				});
				return true;
			}

		});
	}

}
