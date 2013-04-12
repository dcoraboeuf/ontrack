package net.ontrack.extension.jira;

import java.util.Set;

public interface JIRAService {

    Set<String> extractIssueKeysFromMessage(String message);

    String insertIssueUrlsInMessage(String message);

    String getIssueURL(String key);

}
