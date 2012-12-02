package org.alexdalton.jira.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JiraIssue {
    private String summary;
    private int votes;
    private int status;
    private int type;
    private int id;
    private String reporter;
    private String project;
    private String created;
    private String modified;
    private String description;
    private String assignee;
    private String key;
    private int priority;
    private ArrayList<JiraComment> comments;
    private ArrayList<JiraVersion> fixVersions;
    
    public ArrayList<JiraVersion> getFixVersions() {
        return fixVersions;
    }

    public void setFixVersions(ArrayList<JiraVersion> fixVersions) {
        this.fixVersions = fixVersions;
    }

    public static JiraIssue fromMap(Map<String, Object> map) {
        JiraIssue issue = new JiraIssue();
        issue.setSummary((String) map.get("summary"));
        issue.setVotes(Integer.parseInt((String) map.get("votes")));
        issue.setStatus(Integer.parseInt((String) map.get("status")));
        issue.setType(Integer.parseInt((String) map.get("type")));
        issue.setId(Integer.parseInt((String) map.get("id")));
        if (((String) map.get("priority")) != null)
            issue.setPriority(Integer.parseInt((String) map.get("priority")));
        issue.setAssignee((String) map.get("assignee"));
        issue.setReporter((String) map.get("reporter"));
        issue.setProject((String) map.get("project"));
        issue.setCreated((String) map.get("created"));
        issue.setModified((String) map.get("updated"));
        String description = (String) map.get("description");
        if (description != null)
            issue.setDescription(description.replace("\r", "").trim());
        issue.setKey((String) map.get("key"));
        Object[] fv = (Object[]) map.get("fixVersions");
        if (fv != null && fv.length > 0) {
            issue.fixVersions = new ArrayList<JiraVersion>();
            // Log.v("JiraIssue", "fix Versions " + fv);
            for (int i = 0; i<fv.length; i++) {
                issue.fixVersions.add(JiraVersion.fromMap((HashMap<String, Object>) fv[i]));
            }
        }
        Object[] custFields = (Object[]) map.get("customFieldValues");

        return issue;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int sstatus) {
        this.status = sstatus;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setComments(ArrayList<JiraComment> comments) {
        this.comments = comments;
    }

    public ArrayList<JiraComment> getComments() {
        return comments;
    }
}
