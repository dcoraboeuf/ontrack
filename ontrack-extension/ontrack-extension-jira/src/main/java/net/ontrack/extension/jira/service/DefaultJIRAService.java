package net.ontrack.extension.jira.service;

import com.atlassian.httpclient.api.HttpStatus;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.*;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import net.ontrack.core.model.Entity;
import net.ontrack.extension.api.property.PropertiesService;
import net.ontrack.extension.jira.JIRAConfigurationPropertyExtension;
import net.ontrack.extension.jira.JIRAConfigurationService;
import net.ontrack.extension.jira.JIRAExtension;
import net.ontrack.extension.jira.JIRAService;
import net.ontrack.extension.jira.service.model.*;
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

    private final JIRAConfigurationService jiraConfigurationService;
    private final PropertiesService propertiesService;
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

    @Autowired
    public DefaultJIRAService(JIRAConfigurationService jiraConfigurationService, PropertiesService propertiesService, TransactionService transactionService) {
        this.jiraConfigurationService = jiraConfigurationService;
        this.propertiesService = propertiesService;
        this.transactionService = transactionService;
    }

    @Override
    public Set<String> extractIssueKeysFromMessage(int projectId, String message) {
        JIRAConfiguration configuration = getConfigurationForProject(projectId);
        Set<String> result = new HashSet<>();
        Matcher matcher = JIRAConfiguration.ISSUE_PATTERN.matcher(message);
        while (matcher.find()) {
            // Gets the issue
            String issueKey = matcher.group();
            // Adds to the result
            if (configuration.isIssue(issueKey)) {
                result.add(issueKey);
            }
        }
        // OK
        return result;
    }

    @Override
    public String insertIssueUrlsInMessage(int projectId, String message) {
        JIRAConfiguration configuration = getConfigurationForProject(projectId);
        // First, makes the message HTML-ready
        String htmlMessage = StringEscapeUtils.escapeHtml4(message);
        // Replaces each issue by a link
        StringBuffer html = new StringBuffer();
        Matcher matcher = JIRAConfiguration.ISSUE_PATTERN.matcher(htmlMessage);
        while (matcher.find()) {
            String key = matcher.group();
            if (configuration.isIssue(key)) {
                String href = getIssueURL(projectId, key);
                String link = String.format("<a href=\"%s\">%s</a>", href, key);
                matcher.appendReplacement(html, link);
            }
        }
        matcher.appendTail(html);
        // OK
        return html.toString();
    }

    @Override
    public String getIssueURL(int projectId, String key) {
        return getConfigurationForProject(projectId).getIssueURL(key);
    }

    @Override
    public JIRAIssue getIssue(int projectId, String key) {
        try (Transaction tx = transactionService.start()) {
            JIRASession session = tx.getResource(JIRASession.class);
            try {
                // Gets the JIRA issue
                Issue issue = session.getClient().getIssueClient().getIssue(key).claim();

                // Translation of fields
                List<JIRAField> fields = Lists.newArrayList(
                        Iterables.transform(
                                issue.getFields(),
                                new Function<IssueField, JIRAField>() {
                                    @Override
                                    public JIRAField apply(IssueField f) {
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
                        getIssueURL(projectId, issue.getKey()),
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
                Optional<Integer> code = ex.getStatusCode();
                if (code.isPresent() && code.get() == HttpStatus.NOT_FOUND.code) {
                    return null;
                } else {
                    throw ex;
                }
            }
        }
    }

    @Override
    public boolean isIssue(int projectId, String token) {
        return getConfigurationForProject(projectId).isIssue(token);
    }

    @Override
    public String getJIRAURL(int projectId) {
        return getConfigurationForProject(projectId).getUrl();
    }

    // TODO Caching
    @Override
    public JIRAConfiguration getConfigurationForProject(int projectId) {
        return jiraConfigurationService.getConfigurationById(
                Integer.parseInt(
                        propertiesService.getPropertyValue(
                                Entity.PROJECT,
                                projectId,
                                JIRAExtension.EXTENSION,
                                JIRAConfigurationPropertyExtension.NAME
                        ),
                        10)
        );
    }

    private JIRAStatus toStatus(BasicStatus status) {
        return new JIRAStatus(
                status.getName(),
                getStatusIconURL(status)
        );
    }

    private String getStatusIconURL(BasicStatus status) {
        try (Transaction tx = transactionService.start()) {
            Status s = tx.getResource(JIRASession.class).getClient().getMetadataClient().getStatus(status.getSelf()).claim();
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

    private JIRAField toField(IssueField field) {
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
