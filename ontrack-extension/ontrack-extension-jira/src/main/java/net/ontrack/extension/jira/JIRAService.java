package net.ontrack.extension.jira;

import net.ontrack.extension.jira.service.model.JIRAConfiguration;
import net.ontrack.extension.jira.service.model.JIRAIssue;

import java.util.Set;

public interface JIRAService {

    Set<String> extractIssueKeysFromMessage(int projectId, String message);

    String insertIssueUrlsInMessage(String message);

    String getIssueURL(String key);

    JIRAIssue getIssue(String key);

    boolean isIssue(String token);

    String getJIRAURL(int projectId);

    JIRAConfiguration getConfigurationForProject(int projectId);
}
