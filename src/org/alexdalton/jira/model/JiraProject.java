package org.alexdalton.jira.model;

import java.util.ArrayList;
import java.util.Map;

public class JiraProject {
    private String name;
    private String lead;
    private String tag;
    private ArrayList<JiraVersion> versions;

    public JiraProject() {

    }

    public JiraProject(String tag, String name, String lead) {
        this.lead = lead;
        this.tag = tag;
        this.name = name;
    }

    public static JiraProject fromMap(Map<String, Object> map) {
        JiraProject project = new JiraProject();
        project.setName((String) map.get("name"));
        project.setLead((String) map.get("lead"));
        project.setTag((String) map.get("key"));
        return project;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setLead(String lead) {
        this.lead = lead;
    }

    public String getLead() {
        return lead;
    }

    public String getId() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setVersions(ArrayList<JiraVersion> versions) {
        this.versions = versions;
    }

    public ArrayList<JiraVersion> getVersions() {
        return versions;
    }

}
