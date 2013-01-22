package org.alexdalton.jira;

import org.alexdalton.jira.JiraConn.LoginListener;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

final class BaseActivityLoginListener implements LoginListener {

	private final ProgressDialog progressDialog;
	private final Context context;

	public BaseActivityLoginListener(final Context context,
			final ProgressDialog progressDialog) {
		this.progressDialog = progressDialog;
		this.context = context;
	}

	@Override
	public void onSyncProgress(final String message, final int progress,
			final int max) {
		BaseActivity.HANDLER.post(new Runnable() {

			@Override
			public void run() {
				if (progressDialog != null && progressDialog.isShowing()) {
					if (message != null)
					progressDialog.setTitle(message);
					progressDialog.setMax(max);
					progressDialog.setProgress(progress);
				}
			}
		});
	}

	@Override
	public void onLoginError(final Exception e) {
		BaseActivity.HANDLER.post(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(context, "Connection error: " + e, Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void onLoginComplete() {
		BaseActivity.HANDLER.post(new Runnable() {

			@Override
			public void run() {
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
			}
		});
	}
}