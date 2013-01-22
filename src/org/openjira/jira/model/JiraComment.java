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

public class JiraComment {
    int id;
    String author;
    String updateAuthor;
    String body;
    String created;
    String updated;

    public static JiraComment fromMap(Map<String, Object> map) {
        JiraComment ret = new JiraComment();
        ret.setId(Integer.parseInt((String) map.get("id")));
        ret.setAuthor((String) map.get("author"));
        ret.setUpdateAuthor((String) map.get("updateAuthor"));
        String body = (String) map.get("body");
        if (body != null)
            ret.setBody(body.replace("\r", "").trim());
        ret.setCreated((String) map.get("created"));
        ret.setUpdated((String) map.get("updated"));

        return ret;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUpdateAuthor() {
        return updateAuthor;
    }

    public void setUpdateAuthor(String updateAuthor) {
        this.updateAuthor = updateAuthor;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

}
