package org.alexdalton.jira;

import org.alexdalton.jira.JiraConn.LoginListener;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editserverlist);

        // set default intent return for hardware buttons
        Intent mIntent = new Intent();
        setResult(RESULT_CANCELED, mIntent);

        // Get view references
        mServernameText = (EditText) findViewById(R.id.servername);
        mServerUrlText = (EditText) findViewById(R.id.serverurl);
        mUsernameText = (EditText) findViewById(R.id.username);
        mPasswordText = (EditText) findViewById(R.id.password);
        Button submitButton = (Button) findViewById(R.id.submit);

        // Initialize values from Bundle (if it is present)
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.mServerId = extras.getInt(JiraServersDB.KEY_ID);
            String name = extras.getString(JiraServersDB.KEY_NAME);
            String url = extras.getString(JiraServersDB.KEY_URL);
            String username = extras.getString(JiraServersDB.KEY_USER);
            String password = extras.getString(JiraServersDB.KEY_PASSWORD);

            if (name != null) {
                mServernameText.setText(name);
            }
            if (url != null) {
                mServerUrlText.setText(url);
            }
            if (username != null) {
                mUsernameText.setText(username);
            }
            if (password != null) {
                mPasswordText.setText(password);
            }
        }

        // Add button listener
        submitButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Get input values
                String serverName = mServernameText.getText().toString();
                String serverUrl = mServerUrlText.getText().toString();
                String username = mUsernameText.getText().toString();
                String password = mPasswordText.getText().toString();

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
             * @param name The name of the server connection
             * @param url complete server url e.g. http://jira.atlassian.com
             * @param user (optional) username for login attempts
             * @param pass (optional) password for login attempts
             * @return return true on success, false otherwise
             */
            private boolean testConnection(final String name, final String url, final String user, final String pass) {
                final JiraConn conn = new JiraConn(url, user, pass);

                final ProgressDialog dlg = new ProgressDialog(EditServer.this);
                dlg.setMessage("Testing connection");
                dlg.show();

                conn.setOnLoginListener(new LoginListener() {
                    public void onLoginError(final Exception e) {
                        EditServer.this.runOnUiThread(new Runnable() {
                            public void run() {
                                dlg.dismiss();
                                Toast.makeText(EditServer.this, "Error connecting server " + e, Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    public void onLoginComplete() {
                        EditServer.this.runOnUiThread(new Runnable() {
                            public void run() {
                                dlg.dismiss();
                                Toast.makeText(EditServer.this, "Connection successfull ", Toast.LENGTH_LONG).show();
                                Bundle bundle = new Bundle();
                                bundle.putInt(JiraServersDB.KEY_ID, mServerId);
                                bundle.putString(JiraServersDB.KEY_NAME, name);
                                bundle.putString(JiraServersDB.KEY_URL, url);
                                bundle.putString(JiraServersDB.KEY_USER, user);
                                bundle.putString(JiraServersDB.KEY_PASSWORD, pass);

                                // Return Bundle and close
                                Intent mIntent = new Intent();
                                mIntent.putExtras(bundle);
                                setResult(RESULT_OK, mIntent);
                                finish();

                            }
                        });
                    }

                    public void onSyncProgress(String message, int progress, int max) {
                        // TODO Auto-generated method stub

                    }
                });
                conn.testLogin();
                return true;
            }

        });
    }

}
