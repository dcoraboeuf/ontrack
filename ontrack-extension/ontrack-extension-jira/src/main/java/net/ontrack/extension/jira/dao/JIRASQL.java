package net.ontrack.extension.jira.dao;

public interface JIRASQL {
    String JIRA_CONFIGURATION_ALL = "SELECT * FROM EXT_JIRA_CONFIGURATION ORDER BY NAME";
    String JIRA_CONFIGURATION_CREATE = "INSERT INTO EXT_JIRA_CONFIGURATION (NAME, URL, USER, PASSWORD, EXCLUSIONS) VALUES (:name, :url, :user, :password, :exclusions)";
}
