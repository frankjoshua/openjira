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
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.openjira.jira.JiraConn.OnCommentAddedListener;
import org.openjira.jira.JiraConn.OnIssueLoadedListener;
import org.openjira.jira.model.JiraComment;
import org.openjira.jira.model.JiraIssue;
import org.openjira.jira.model.JiraVersion;
import org.openjira.jira.utils.LoadImageAsync;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class IssueDetails extends Activity implements OnIssueLoadedListener, OnCommentAddedListener {
    JiraApp app;
    JiraIssue currentIssue;
    private JiraConn conn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.issuedetails);

        app = JiraApp.get();
        conn = app.getCurrentConnection();
        Uri data = getIntent().getData();

        String key = data.getQueryParameter("key");

        findViewById(R.id.progress).setVisibility(View.VISIBLE);

        conn.getIssueAsync(key, true, this);

        findViewById(R.id.addcomment).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String commentText = ((EditText) findViewById(R.id.addcommenttext)).getText().toString();
                if (commentText != null) {
                    findViewById(R.id.progress).setVisibility(View.VISIBLE);
                    conn.addCommentAsync(currentIssue.getKey(), commentText, IssueDetails.this);
                }
            }
        });
    }

    public void displayIssue(JiraIssue issue) {

        TextView tv;
        currentIssue = issue;
        tv = (TextView) findViewById(R.id.key);
        tv.setText(issue.getKey());
        // tv = (TextView) findViewById(R.id.count);
        // tv.setText("" + (app.getCurrentIssue() + 1) + "/" +
        // issueList.size());
        tv = (TextView) findViewById(R.id.summary);
        tv.setText(issue.getSummary());
        tv = (TextView) findViewById(R.id.status);
        tv.setText(conn.getStatusLabel(issue.getStatus()));
        tv = (TextView) findViewById(R.id.priority);
        tv.setText(conn.getPriorityLabel(issue.getPriority()));
        tv = (TextView) findViewById(R.id.type);
        tv.setText(conn.getTypeLabel(issue.getType()));
        tv = (TextView) findViewById(R.id.reporter);
        tv.setText(issue.getReporter());
        tv = (TextView) findViewById(R.id.assignee);
        tv.setText(issue.getAssignee());
        tv = (TextView) findViewById(R.id.date);
        tv.setText(issue.getCreated());
        tv = (TextView) findViewById(R.id.description);
        tv.setText(issue.getDescription());

        if (issue.getFixVersions() != null) {
            ArrayList<JiraVersion> versions = issue.getFixVersions();
            String fixvstr = new String();
            if (versions.size() > 0) {
                for (int i = 0; i < versions.size() - 1; i++) {
                    fixvstr += versions.get(i) + ", ";
                }
                fixvstr += versions.get(versions.size() - 1).getName();
                ((TextView) findViewById(R.id.fixv)).setText(fixvstr);
            } else {
                ((TextView) findViewById(R.id.fixv)).setText("None");
            }
        } else {
            ((TextView) findViewById(R.id.fixv)).setText("None");
        }

        ImageView iv;
        iv = (ImageView) findViewById(R.id.typeimg);
        LoadImageAsync.setImageViewAsync(conn.getTypeIcon(issue.getType()), iv, R.drawable.priority_unknown, new LoadImageAsync.LoadBitmapListener() {
            public void bitmapLoaded(Bitmap b, View v) {
                ((ImageView) v).setImageBitmap(b);
            }
        }, this);
        iv = (ImageView) findViewById(R.id.statusimg);
        LoadImageAsync.setImageViewAsync(conn.getStatusIcon(issue.getStatus()), iv, R.drawable.priority_unknown, new LoadImageAsync.LoadBitmapListener() {
            public void bitmapLoaded(Bitmap b, View v) {
                ((ImageView) v).setImageBitmap(b);
            }
        }, this);
        iv = (ImageView) findViewById(R.id.priorityimg);
        LoadImageAsync.setImageViewAsync(conn.getPriorityIcon(issue.getPriority()), iv, R.drawable.priority_unknown, new LoadImageAsync.LoadBitmapListener() {
            public void bitmapLoaded(Bitmap b, View v) {
                ((ImageView) v).setImageBitmap(b);
            }
        }, this);

        try {
            ArrayList<JiraComment> comments = issue.getComments();

            LinearLayout l = (LinearLayout) findViewById(R.id.commentlayout);
            l.removeAllViews();
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (comments != null) {
                for (int i = 0; i < comments.size(); i++) {
                    RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.commentlistitem, null);
                    JiraComment comment = comments.get(i);
                    ((TextView) view.findViewById(R.id.comment)).setText(comment.getBody());
                    ((TextView) view.findViewById(R.id.user)).setText(comment.getAuthor());
                    ((TextView) view.findViewById(R.id.date)).setText(comment.getUpdated());
                    l.addView(view);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (issue.getDescription() == null) {
            findViewById(R.id.description).setVisibility(View.GONE);
            findViewById(R.id.descriptionlabel).setVisibility(View.GONE);
        } else {
            findViewById(R.id.description).setVisibility(View.VISIBLE);
            findViewById(R.id.descriptionlabel).setVisibility(View.VISIBLE);
        }
        if (issue.getComments() == null) {
            findViewById(R.id.commentlayout).setVisibility(View.GONE);
            findViewById(R.id.commentslabel).setVisibility(View.GONE);
        } else {
            findViewById(R.id.commentlayout).setVisibility(View.VISIBLE);
            findViewById(R.id.commentslabel).setVisibility(View.VISIBLE);
        }

        findViewById(R.id.modify).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                showChangeableItems();
            }
        });
    }

    public void showChangeableItems() {
        AlertDialog.Builder db = new AlertDialog.Builder(this);
        String[] items = {"Status", "Priority", "Type"};
        db.setTitle("Choose item to modify");
        db.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        changeStatus();
                        break;
                    case 1:
                        changePriority();
                        break;
                    case 2:
                        changeType();
                        break;
                }
            }
        });
        db.create().show();
    }

    protected void showChooser(String title, ArrayList<String> items, final ArrayList<Integer> ids, final String field) {
        AlertDialog.Builder db = new AlertDialog.Builder(this);
        db.setTitle(title);
        db.setItems(items.toArray(new String[0]), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Map<String, Vector> map = new HashMap<String, Vector>();
                Vector<Object> tmp = new Vector<Object>();
                tmp.add(ids.get(which).toString());
                map.put(field, tmp);
                conn.updateIssue(currentIssue.getKey(), map);
                Toast.makeText(IssueDetails.this, "Issue Updated", Toast.LENGTH_SHORT).show();
                conn.getIssueAsync(currentIssue.getKey(), true, IssueDetails.this);
            }
        });
        db.create().show();
    }

    protected void changeType() {
        final ArrayList<Integer> ids = new ArrayList<Integer>();
        ArrayList<String> items = conn.getTypeLabels(ids);
        showChooser("Select new type", items, ids, "issuetype");
    }

    protected void changePriority() {
        final ArrayList<Integer> ids = new ArrayList<Integer>();
        ArrayList<String> items = conn.getPriorityLabels(ids);
        showChooser("Select new type", items, ids, "priority");
    }

    protected void changeStatus() {
        final ArrayList<Integer> ids = new ArrayList<Integer>();
        ArrayList<String> items = conn.getStatusesLabels(ids);
        showChooser("Select new status", items, ids, "issuestatus");
    }

    public void onIssueLoaded(final JiraIssue issue) {
        runOnUiThread(new Runnable() {
            public void run() {
                findViewById(R.id.progress).setVisibility(View.GONE);
                displayIssue(issue);
            }
        });
    }

    public void onCommentAdded(boolean success) {
        if (success) {
            conn.getIssueAsync(currentIssue.getKey(), true, IssueDetails.this);
            IssueDetails.this.runOnUiThread(new Runnable() {
                public void run() {
                    ((EditText) findViewById(R.id.addcommenttext)).setText("");
                }
            });
        } else {
            findViewById(R.id.progress).setVisibility(View.GONE);
        }
    }

}
