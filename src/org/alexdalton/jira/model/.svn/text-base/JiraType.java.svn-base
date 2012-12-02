package org.alexdalton.jira.model;

import java.util.Map;

import android.graphics.Bitmap;
import android.net.Uri;

public class JiraType {
    private String name;
    private String description;
    private String icon;
    private int id;
    private Bitmap b;

    public JiraType() {
    }

    public JiraType(String id, String name, String description, String icon) {
        this.id = Integer.parseInt(id);
        this.name = name;
        this.description = description;
        this.icon = icon;
    }

    public static JiraType fromMap(Map<String, Object> map, String baseUri) {
        final JiraType type = new JiraType();
        type.setName((String) map.get("name"));
        type.setDescription((String) map.get("description"));
        type.setId(Integer.parseInt((String) map.get("id")));
        Uri uri = Uri.parse((String) map.get("icon"));
        String url = baseUri + uri.getPath();
        type.setIcon(url);
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }

    public void setBitmap(Bitmap b) {
        this.b = b;
    }

    public Bitmap getBitmap() {
        return b;
    }

    @Override
    public String toString() {
        return "Type: '" + id + "' name: '" + name + "' icon: '" + icon + "'";
    }

}
