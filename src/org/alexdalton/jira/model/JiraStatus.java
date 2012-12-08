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
package org.alexdalton.jira.model;

import java.util.Map;

import android.graphics.Bitmap;
import android.net.Uri;

public class JiraStatus {
    private String name;
    private String description;
    private String icon;
    private int id;
    private Bitmap bitmap;

    public JiraStatus() {

    }

    public JiraStatus(String id, String name, String description, String icon) {
        this.id = Integer.parseInt(id);
        this.name = name;
        this.description = description;
        this.icon = icon;
    }

    public static JiraStatus fromMap(Map<String, Object> map, String baseUri) {
        final JiraStatus status = new JiraStatus();
        status.setName((String) map.get("name"));
        status.setDescription((String) map.get("description"));
        status.setId(Integer.parseInt((String) map.get("id")));
        Uri uri = Uri.parse((String) map.get("icon"));
        String url = baseUri + uri.getPath();
        status.setIcon(url);
        return status;
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
        return "Status: '" + id + "' name: '" + name + "' icon: '" + icon + "'";
    }

}
