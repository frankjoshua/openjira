package org.alexdalton.jira.model;

import java.util.Map;

public class JiraResolution {
    int id;
    String description;
    String name;

    public static JiraResolution fromMap(Map<String, Object> map) {
        JiraResolution ret = new JiraResolution();
        ret.setId(Integer.parseInt((String) map.get("id")));
        ret.setName((String) map.get("name"));
        ret.setDescription((String) map.get("description"));
        return ret;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
