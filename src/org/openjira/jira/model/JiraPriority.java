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
package org.openjira.jira.model;

import java.util.Map;

import org.openjira.jira.JiraApp;
import org.openjira.jira.R;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

public class JiraPriority {
    private String name;
    private String description;
    private String icon;
    private int id;
    private Bitmap bitmap;

    public JiraPriority() {

    }

    public JiraPriority(String id, String name, String description, String icon) {
        this.id = Integer.parseInt(id);
        this.name = name;
        this.description = description;
        this.icon = icon;
    }

    public static JiraPriority fromMap(Map<String, Object> map, String baseUri) {
        final JiraPriority priority = new JiraPriority();
        priority.setName((String) map.get("name"));
        priority.setDescription((String) map.get("description"));
        priority.setId(Integer.parseInt((String) map.get("id")));
        priority.setBitmap(BitmapFactory.decodeResource(JiraApp.get().getResources(), R.drawable.priority_unknown));
        Uri uri = Uri.parse((String) map.get("icon"));
        String url = baseUri + uri.getPath();
        priority.setIcon(url);
        return priority;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    @Override
    public String toString() {
        return "Priority: '" + id + "' name: '" + name + "' icon: '" + icon + "'";
    }
}
