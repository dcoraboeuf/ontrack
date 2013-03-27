package net.ontrack.extension.jira.service;

import net.ontrack.extension.jira.JIRAExtension;
import net.ontrack.extension.jira.JIRAService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

@Service
public class DefaultJIRAService implements JIRAService {

    @Override
    public Set<String> extractIssueKeysFromMessage(String message) {
        Set<String> result = new HashSet<>();
        Matcher matcher = JIRAExtension.ISSUE_PATTERN.matcher(message);
        while (matcher.find()) {
            // Gets the issue
            String issueKey = matcher.group();
            // Adds to the result
            result.add(issueKey);
        }
        // OK
        return result;
    }
}
