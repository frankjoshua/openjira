package org.alexdalton.jira;

import java.util.ArrayList;

import org.alexdalton.jira.model.JiraIssue;
import org.alexdalton.jira.utils.LoadImageAsync;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class IssueListAdapter extends BaseAdapter {
    ArrayList<JiraIssue> issues;
    LayoutInflater inflater;
    Activity activity;
    JiraConn conn;

    public IssueListAdapter(Activity context, JiraConn conn, ArrayList<JiraIssue> issues) {
        this.issues = issues;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        activity = context;
        this.conn = conn;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return issues.size();
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return issues.get(position);
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public View getView(int position, View recycledView, ViewGroup arg2) {
        View view = null;

        if (recycledView != null) {
            view = recycledView;
        } else {
            view = inflater.inflate(R.layout.issuelistitem, null);
        }
        display(position, view);
        return view;
    }

    private void display(int position, View view) {
        JiraIssue issue = issues.get(position);
        ((TextView) view.findViewById(R.id.title)).setText(issue.getSummary());
        ((TextView) view.findViewById(R.id.project)).setText(issue.getKey());
        // ((TextView)
        // view.findViewById(R.id.type)).setText(JiraApp.get().getTypeLabel(issue.getType()));
        String icon = conn.getPriorityIcon(issue.getPriority());
        if (icon != null && icon.startsWith("http"))
            LoadImageAsync.setImageViewAsync(icon, (ImageView) view.findViewById(R.id.priority), R.drawable.priority_unknown, new LoadImageAsync.LoadBitmapListener() {
                public void bitmapLoaded(Bitmap b, View v) {
                    ((ImageView) v).setImageBitmap(b);
                }
            }, activity);
        else
            ((ImageView) view.findViewById(R.id.priority)).setImageResource(R.drawable.priority_unknown);

        icon = conn.getTypeIcon(issue.getType());
        if (icon != null && icon.startsWith("http"))
            LoadImageAsync.setImageViewAsync(icon, (ImageView) view.findViewById(R.id.typeimg), R.drawable.priority_unknown, new LoadImageAsync.LoadBitmapListener() {
                public void bitmapLoaded(Bitmap b, View v) {
                    ((ImageView) v).setImageBitmap(b);
                }
            }, activity);
        else
            ((ImageView) view.findViewById(R.id.priority)).setImageResource(R.drawable.priority_unknown);

        icon = conn.getStatusIcon(issue.getStatus());
        if (icon != null && icon.startsWith("http"))
            LoadImageAsync.setImageViewAsync(icon, (ImageView) view.findViewById(R.id.statusimg), R.drawable.priority_unknown, new LoadImageAsync.LoadBitmapListener() {
                public void bitmapLoaded(Bitmap b, View v) {
                    ((ImageView) v).setImageBitmap(b);
                }
            }, activity);
        else
            ((ImageView) view.findViewById(R.id.priority)).setImageResource(R.drawable.priority_unknown);
    }

}
