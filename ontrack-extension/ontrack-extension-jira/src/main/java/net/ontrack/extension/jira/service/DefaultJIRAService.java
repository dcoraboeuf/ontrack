package net.ontrack.extension.jira.service;

import net.ontrack.extension.jira.JIRAConfigurationExtension;
import net.ontrack.extension.jira.JIRAExtension;
import net.ontrack.extension.jira.JIRAService;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

@Service
public class DefaultJIRAService implements JIRAService {

    private final JIRAConfigurationExtension configurationExtension;

    @Autowired
    public DefaultJIRAService(JIRAConfigurationExtension configurationExtension) {
        this.configurationExtension = configurationExtension;
    }

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

    @Override
    public String insertIssueUrlsInMessage(String message) {
        // First, makes the message HTML-ready
        String htmlMessage = StringEscapeUtils.escapeHtml4(message);
        // Replaces each issue by a link
        StringBuffer html = new StringBuffer();
        Matcher matcher = JIRAExtension.ISSUE_PATTERN.matcher(htmlMessage);
        while (matcher.find()) {
            String key = matcher.group();
            String href = getIssueURL(key);
            String link = String.format("<a href=\"%s\">%s</a>", href, key);
            matcher.appendReplacement(html, link);
        }
        matcher.appendTail(html);
        // OK
        return html.toString();
    }

    @Override
    public String getIssueURL(String key) {
        return configurationExtension.getIssueURL(key);
    }
}
