package net.ontrack.extension.jira;

import net.ontrack.extension.jira.service.model.JIRAConfiguration;
import net.ontrack.extension.jira.service.model.JIRAIssue;

import java.util.Set;

public interface JIRAService {

    Set<String> extractIssueKeysFromMessage(int projectId, String message);

    String insertIssueUrlsInMessage(int projectId, String message);

    String getIssueURL(int projectId, String key);

    JIRAIssue getIssue(int projectId, String key);

    boolean isIssue(int projectId, String token);

    String getJIRAURL(int projectId);

    JIRAConfiguration getConfigurationForProject(int projectId);
}
