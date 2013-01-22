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

import org.openjira.jira.JiraConn.OnSearchCompleteListener;
import org.openjira.jira.model.JiraIssue;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class Search extends ListActivity implements OnSearchCompleteListener {
    private JiraApp app;
    private JiraConn conn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.search);

        app = JiraApp.get();
        conn = app.getCurrentConnection();

        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            String query = intent.getData().getQueryParameter("query");
            EditText et = (EditText) findViewById(R.id.query);
            et.setText(query);
            if (query != null && query.length() > 0) {
                findViewById(R.id.progress).setVisibility(View.VISIBLE);
                conn.searchIssueAsync(query, Search.this);
            }
        }

        findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText et = (EditText) findViewById(R.id.query);
                String query = et.getText().toString();
                if (query != null && query.length() > 0) {
                    findViewById(R.id.progress).setVisibility(View.VISIBLE);
                    conn.searchIssueAsync(query, Search.this);
                }
            }
        });
    }

    private void showIssues(ArrayList<JiraIssue> issueList) {
        IssueListAdapter a = new IssueListAdapter(this, conn, issueList);
        setListAdapter(a);
        TextView tv = (TextView) findViewById(R.id.count);
        tv.setText("" + issueList.size());
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

    public void onSearchComplete(final ArrayList<JiraIssue> issues) {
        runOnUiThread(new Runnable() {
            public void run() {
                findViewById(R.id.progress).setVisibility(View.GONE);
                showIssues(issues);
            }
        });
    }
}
