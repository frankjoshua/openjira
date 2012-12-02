package org.alexdalton.jira.model;

import java.util.Map;

import android.graphics.Bitmap;
import android.net.Uri;

public class JiraStatus {
    private String name;
    private String description;
    private String icon;
    private int id;
    private Bitmap bitmap;

    public JiraStatus() {

    }

    public JiraStatus(String id, String name, String description, String icon) {
        this.id = Integer.parseInt(id);
        this.name = name;
        this.description = description;
        this.icon = icon;
    }

    public static JiraStatus fromMap(Map<String, Object> map, String baseUri) {
        final JiraStatus status = new JiraStatus();
        status.setName((String) map.get("name"));
        status.setDescription((String) map.get("description"));
        status.setId(Integer.parseInt((String) map.get("id")));
        Uri uri = Uri.parse((String) map.get("icon"));
        String url = baseUri + uri.getPath();
        status.setIcon(url);
        return status;
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

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    @Override
    public String toString() {
        return "Status: '" + id + "' name: '" + name + "' icon: '" + icon + "'";
    }

}
