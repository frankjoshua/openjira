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

public class JiraFilter {
    private String name;
    private String id;

    public JiraFilter() {
    }

    public JiraFilter(String id, String name) {
        this.name = name;
        this.id = id;
    }

    public static JiraFilter fromMap(Map<String, Object> map) {
        JiraFilter filter = new JiraFilter();
        filter.setName((String) map.get("name"));
        filter.setId((String) map.get("id"));
        return filter;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

}
