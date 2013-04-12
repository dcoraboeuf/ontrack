package net.ontrack.extension.jira.service;

import com.atlassian.jira.rest.client.NullProgressMonitor;
import com.atlassian.jira.rest.client.RestClientException;
import com.atlassian.jira.rest.client.domain.BasicUser;
import com.atlassian.jira.rest.client.domain.Issue;
import net.ontrack.extension.jira.JIRAConfigurationExtension;
import net.ontrack.extension.jira.JIRAExtension;
import net.ontrack.extension.jira.JIRAService;
import net.ontrack.extension.jira.service.model.JIRAIssue;
import net.ontrack.extension.jira.tx.JIRASession;
import net.ontrack.tx.Transaction;
import net.ontrack.tx.TransactionService;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

@Service
public class DefaultJIRAService implements JIRAService {

    private final JIRAConfigurationExtension configurationExtension;
    private final TransactionService transactionService;

    @Autowired
    public DefaultJIRAService(JIRAConfigurationExtension configurationExtension, TransactionService transactionService) {
        this.configurationExtension = configurationExtension;
        this.transactionService = transactionService;
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

    @Override
    public JIRAIssue getIssue(String key) {
        try (Transaction tx = transactionService.start()) {
            JIRASession session = tx.getResource(JIRASession.class);
            try {
                Issue issue = session.getClient().getIssueClient().getIssue(key, new NullProgressMonitor());
                // Creates the JIRA issue
                // TODO Translation of fields
                // TODO Status
                return new JIRAIssue(
                        getIssueURL(issue.getKey()),
                        issue.getKey(),
                        issue.getSummary(),
                        getUserName(issue.getAssignee()),
                        issue.getUpdateDate()
                );
            } catch (RestClientException ex) {
                if ("Issue Does Not Exist".equals(ex.getMessage())) {
                    return null;
                } else {
                    throw ex;
                }
            }
        }
    }

    private String getUserName(BasicUser user) {
        if (user != null) {
            return user.getDisplayName();
        } else {
            return "";
        }
    }
}
