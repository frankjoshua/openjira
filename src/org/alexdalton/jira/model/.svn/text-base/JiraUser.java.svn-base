package org.alexdalton.jira.model;

import java.util.Map;

public class JiraUser {
    String name;
    String fullName;
    String email;

    public static JiraUser fromMap(Map<String, Object> map) {
        JiraUser user = new JiraUser();
        user.setName((String) map.get("name"));
        user.setFullName((String) map.get("fullname"));
        user.setEmail((String) map.get("email"));
        return user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
