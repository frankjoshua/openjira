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

import org.openjira.jira.model.JiraVersion;
import org.xmlrpc.android.XMLRPCException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CreateIssue extends Activity {
    JiraApp app;
    private JiraConn conn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createissue);
        app = JiraApp.get();
        ArrayAdapter<String> adapter;
        conn = app.getCurrentConnection();
        ArrayList<String> list;
        final ArrayList<String> projectIds = new ArrayList<String>();
        final ArrayList<Integer> typeIds = new ArrayList<Integer>();
        final ArrayList<Integer> prioIds = new ArrayList<Integer>();
        
        final Spinner spProject = (Spinner) findViewById(R.id.project);
        list = conn.getProjectsLabels(projectIds);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProject.setAdapter(adapter);
        spProject.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView< ? > arg0, View arg1, int arg2, long arg3) {
                ArrayList<JiraVersion> versions = conn.getProject(projectIds.get(arg2)).getVersions();
                ArrayList<String> vlabels = new ArrayList<String>();
                for (int i = 0; i < versions.size(); i++) {
                    JiraVersion v = versions.get(i);
                    if (!v.isReleased())
                        vlabels.add(versions.get(i).getName());
                }
                ArrayAdapter<String> a = new ArrayAdapter<String>(CreateIssue.this, android.R.layout.simple_spinner_item, vlabels);
                a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                ((Spinner)findViewById(R.id.fixv)).setAdapter(a);
            }

            public void onNothingSelected(AdapterView< ? > arg0) {
                // TODO Auto-generated method stub
                
            }
            
        });
        
        final Spinner spType = (Spinner) findViewById(R.id.type);
        list = conn.getTypeLabels(typeIds);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(adapter);
        
        final Spinner spPrio = (Spinner) findViewById(R.id.priority);
        list = conn.getPriorityLabels(prioIds);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPrio.setAdapter(adapter);
        
        Button b = (Button) findViewById(R.id.submit);
        b.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                final String summary = ((TextView) findViewById(R.id.summary)).getText().toString();
                final String description = ((TextView) findViewById(R.id.description)).getText().toString();
                dialog = new ProgressDialog(CreateIssue.this);
                dialog.setMessage("Creating issue");
                dialog.show();
                new Thread(new Runnable() {
                        public void run() {
                            // TODO Auto-generated method stub
                        try {
                            conn.createIssue(projectIds.get(spProject.getSelectedItemPosition()), prioIds.get(spPrio.getSelectedItemPosition()).toString(), typeIds.get(
                                    spType.getSelectedItemPosition()).toString(), null, null, summary, description);
                            issueCreated(true);
                            finish();
                        } catch (XMLRPCException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            issueCreated(false);
                        }
                    }
                }).start();
            }
        });
    }

    private ProgressDialog dialog;
    
    private void issueCreated(final boolean success) {
        runOnUiThread(new Runnable() {
            public void run() {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (success) {
                    Toast.makeText(CreateIssue.this, "Issue succesfully created", Toast.LENGTH_SHORT);
                    finish();
                }
            }
        });
    }
}
