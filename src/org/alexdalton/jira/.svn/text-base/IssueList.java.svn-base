package org.alexdalton.jira;

import java.util.ArrayList;

import org.alexdalton.jira.JiraConn.OnIssueListLoadedListener;
import org.alexdalton.jira.model.JiraIssue;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

public class IssueList extends ListActivity implements OnIssueListLoadedListener {
    private JiraApp app;
    private JiraConn conn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.issuelist);

        app = JiraApp.get();
        conn = app.getCurrentConnection();

        String filter;
        String filterName;
        Intent intent = getIntent();

        if (intent == null || !intent.hasExtra("filter")) {
            finish();
            return;
        } else {
            filter = intent.getStringExtra("filter");
            filterName = intent.getStringExtra("filterName");
            TextView tv = (TextView) findViewById(R.id.filter);
            tv.setText(filterName);
        }

        if (filter != null) {
            findViewById(R.id.progress).setVisibility(View.VISIBLE);
            conn.getIssueListAsync(filter, this);
        } else if (conn.getIssueList() != null) {
            showIssues(conn.getIssueList());
        }
    }

    private void showIssues(ArrayList<JiraIssue> issueList) {
        IssueListAdapter a = new IssueListAdapter(this, conn, issueList);
        setListAdapter(a);
        TextView tv = (TextView) findViewById(R.id.count);
        tv.setText("" + issueList.size());
    }

    public void onIssueListLoaded(final ArrayList<JiraIssue> issueList) {
        runOnUiThread(new Runnable() {
            public void run() {
                findViewById(R.id.progress).setVisibility(View.GONE);
                showIssues(issueList);
            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String key = ((JiraIssue) getListAdapter().getItem(position)).getKey();
        Intent intent = new Intent(this, IssueDetails.class).setData(Uri.parse("jira://openIssue?key=" + key));
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (conn.getIssueList() != null)
            showIssues(conn.getIssueList());
    }
}
