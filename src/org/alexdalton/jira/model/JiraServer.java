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

public class JiraServer {

    private int _id;
    private String name;
    private String url;
    private String user;
    private String password;

    public JiraServer(int _id, String name, String url, String user, String password) {
        super();
        this._id = _id;
        this.name = name;
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public JiraServer(String name, String url, String user, String password) {
        super();
        this.name = name;
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(Integer id) {
        _id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
