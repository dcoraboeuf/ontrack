package net.ontrack.extension.jira.service;

import com.atlassian.jira.rest.client.NullProgressMonitor;
import com.atlassian.jira.rest.client.RestClientException;
import com.atlassian.jira.rest.client.domain.*;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import net.ontrack.extension.jira.JIRAConfigurationExtension;
import net.ontrack.extension.jira.JIRAExtension;
import net.ontrack.extension.jira.JIRAService;
import net.ontrack.extension.jira.service.model.JIRAField;
import net.ontrack.extension.jira.service.model.JIRAIssue;
import net.ontrack.extension.jira.service.model.JIRAStatus;
import net.ontrack.extension.jira.service.model.JIRAVersion;
import net.ontrack.extension.jira.tx.JIRASession;
import net.ontrack.tx.Transaction;
import net.ontrack.tx.TransactionService;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

@Service
public class DefaultJIRAService implements JIRAService {

    private final JIRAConfigurationExtension configurationExtension;
    private final TransactionService transactionService;
    /**
     * Conversion to a JIRAVersion from a REST JIRA version
     */
    private final Function<Version, JIRAVersion> versionFunction = new Function<Version, JIRAVersion>() {
        @Override
        public JIRAVersion apply(Version v) {
            return new JIRAVersion(
                    v.getName(),
                    v.isReleased()
            );
        }
    };
    private final NullProgressMonitor progressMonitor = new NullProgressMonitor();

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
                // Gets the JIRA issue
                Issue issue = session.getClient().getIssueClient().getIssue(key, progressMonitor);

                // Translation of fields
                List<JIRAField> fields = Lists.newArrayList(
                        Iterables.transform(
                                issue.getFields(),
                                new Function<Field, JIRAField>() {
                                    @Override
                                    public JIRAField apply(Field f) {
                                        return toField(f);
                                    }
                                }
                        )
                );

                // Versions
                List<JIRAVersion> affectedVersions = toVersions(issue.getAffectedVersions());
                List<JIRAVersion> fixVersions = toVersions(issue.getFixVersions());

                // Status
                JIRAStatus status = toStatus(issue.getStatus());

                // Formatted JIRA issue
                return new JIRAIssue(
                        getIssueURL(issue.getKey()),
                        issue.getKey(),
                        issue.getSummary(),
                        status,
                        getUserName(issue.getAssignee()),
                        issue.getUpdateDate(),
                        fields,
                        affectedVersions,
                        fixVersions
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

    @Override
    public boolean isIssue(String token) {
        // TODO List of excluded projects (see #97)
        return JIRAExtension.ISSUE_PATTERN.matcher(token).matches();
    }

    private JIRAStatus toStatus(BasicStatus status) {
        return new JIRAStatus(
                status.getName(),
                getStatusIconURL(status)
        );
    }

    private String getStatusIconURL(BasicStatus status) {
        try (Transaction tx = transactionService.start()) {
            Status s = tx.getResource(JIRASession.class).getClient().getMetadataClient().getStatus(status.getSelf(), progressMonitor);
            return s.getIconUrl().toString();
        }
    }

    private List<JIRAVersion> toVersions(Iterable<Version> versions) {
        if (versions == null) {
            return Collections.emptyList();
        } else {
            return Lists.newArrayList(
                    Iterables.transform(
                            versions,
                            versionFunction
                    )
            );
        }
    }

    private JIRAField toField(Field field) {
        return new JIRAField(
                field.getName(),
                field.getType(),
                toFieldValue(field.getValue())
        );
    }

    private String toFieldValue(Object value) {
        if (value == null) {
            return "";
        } else if (value.getClass().isArray()) {
            return StringUtils.join((Object[]) value, ",");
        } else {
            return value.toString();
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
