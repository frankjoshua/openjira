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
package org.alexdalton.jira;

import org.alexdalton.jira.model.JiraServerInfo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class About extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.about);

        ComponentName comp = new ComponentName(this, this.getClass());
        PackageInfo pinfo = null;
        try {
            pinfo = getPackageManager().getPackageInfo(comp.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        TextView tv = (TextView) findViewById(R.id.appversion);
        tv.setText(getResources().getString(R.string.app_name) + " version " + (pinfo != null ? pinfo.versionName : ""));

        JiraServerInfo info = JiraApp.get().getCurrentConnection().serverInfo;
        if (info != null) {
            tv = (TextView) findViewById(R.id.baseurl);
            tv.setText(info.getBaseUrl());
            tv = (TextView) findViewById(R.id.edition);
            tv.setText(info.getEdition());
            tv = (TextView) findViewById(R.id.builddate);
            tv.setText(info.getBuildDate());
            tv = (TextView) findViewById(R.id.buildnumber);
            tv.setText(info.getBuildNumber());
            tv = (TextView) findViewById(R.id.srvversion);
            tv.setText(info.getVersion());
        }
    }

}
