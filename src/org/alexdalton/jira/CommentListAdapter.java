package org.alexdalton.jira;

import java.util.ArrayList;

import org.alexdalton.jira.model.JiraComment;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CommentListAdapter extends BaseAdapter {
    ArrayList<JiraComment> comments;
    LayoutInflater inflater;
    Activity activity;

    public CommentListAdapter(Activity context, ArrayList<JiraComment> comments) {
        this.comments = comments;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        activity = context;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return comments.size();
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return comments.get(position);
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
            view = inflater.inflate(R.layout.commentlistitem, null);
        }
        display(position, view);
        return view;
    }

    private void display(int position, View view) {
        JiraComment comment = comments.get(position);
        ((TextView) view.findViewById(R.id.comment)).setText(comment.getBody());
        ((TextView) view.findViewById(R.id.user)).setText(comment.getAuthor());
        ((TextView) view.findViewById(R.id.date)).setText(comment.getUpdated());
    }

}
