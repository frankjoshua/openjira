package org.alexdalton.jira.model;

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
