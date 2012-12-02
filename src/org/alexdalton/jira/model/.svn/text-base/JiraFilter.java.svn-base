package org.alexdalton.jira.model;

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
