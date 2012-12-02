package org.alexdalton.jira.model;

import java.util.HashMap;

public class JiraVersion {
    private String name;
    private String releaseDate;
    private int sequence;
    private boolean released;
    private int id;
    private boolean archived;

    public JiraVersion() {
        super();
    }

    public JiraVersion(int id, String name, String releaseDate, int sequence, boolean released, boolean archived) {
        super();
        this.name = name;
        this.releaseDate = releaseDate;
        this.sequence = sequence;
        this.released = released;
        this.id = id;
        this.archived = archived;
    }

    public static JiraVersion fromMap(HashMap<String, Object> map) {
        JiraVersion version = new JiraVersion();
        version.setId(Integer.parseInt((String) map.get("id")));
        version.setName((String) map.get("name"));
        version.setReleaseDate((String) map.get("releaseDate"));
        if (map.containsKey("sequence"))
            version.setSequence(Integer.parseInt((String) map.get("sequence")));

        version.setArchived(((String) map.get("archived")).equals("true") ? true : false);
        version.setReleased(((String) map.get("released")).equals("true") ? true : false);
        return version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public boolean isReleased() {
        return released;
    }

    public void setReleased(boolean released) {
        this.released = released;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

}
