package org.alexdalton.jira.model;

import java.util.Map;

public class JiraServerInfo {
    private String baseUrl;
    private String edition;
    private String buildNumber;
    private String buildDate;
    private String version;

    public static JiraServerInfo fromMap(Map<String, Object> map) {
        JiraServerInfo info = new JiraServerInfo();
        info.setBaseUrl((String) map.get("baseUrl"));
        info.setEdition((String) map.get("edition"));
        info.setBuildNumber((String) map.get("buildNumber"));
        info.setBuildDate((String) map.get("buildDate"));
        info.setVersion((String) map.get("version"));
        return info;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(String buildNumber) {
        this.buildNumber = buildNumber;
    }

    public String getBuildDate() {
        return buildDate;
    }

    public void setBuildDate(String buildDate) {
        this.buildDate = buildDate;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
