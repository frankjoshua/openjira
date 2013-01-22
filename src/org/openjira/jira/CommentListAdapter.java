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

import org.openjira.jira.model.JiraComment;

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
