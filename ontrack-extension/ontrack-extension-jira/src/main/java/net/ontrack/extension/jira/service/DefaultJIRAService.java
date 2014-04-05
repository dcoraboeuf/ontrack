package net.ontrack.extension.jira.service;

import com.atlassian.httpclient.api.HttpStatus;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.*;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import net.ontrack.core.model.Entity;
import net.ontrack.extension.api.ExtensionManager;
import net.ontrack.extension.api.property.PropertiesService;
import net.ontrack.extension.issue.IssueServiceConfig;
import net.ontrack.extension.issue.IssueServiceConfigSummary;
import net.ontrack.extension.issue.support.AbstractIssueService;
import net.ontrack.extension.jira.JIRAConfigurationPropertyExtension;
import net.ontrack.extension.jira.JIRAConfigurationService;
import net.ontrack.extension.jira.JIRAExtension;
import net.ontrack.extension.jira.JIRAService;
import net.ontrack.extension.jira.service.model.*;
import net.ontrack.extension.jira.tx.JIRASession;
import net.ontrack.extension.jira.tx.JIRASessionFactory;
import net.ontrack.tx.Transaction;
import net.ontrack.tx.TransactionResourceProvider;
import net.ontrack.tx.TransactionService;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;

import static java.lang.String.format;

@Service
public class DefaultJIRAService extends AbstractIssueService implements JIRAService {

    private final JIRAConfigurationService jiraConfigurationService;
    private final PropertiesService propertiesService;
    private final TransactionService transactionService;
    private final JIRASessionFactory jiraSessionFactory;
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
    public DefaultJIRAService(ExtensionManager extensionManager, JIRAConfigurationService jiraConfigurationService, PropertiesService propertiesService, TransactionService transactionService, JIRASessionFactory jiraSessionFactory) {
        super(JIRAExtension.EXTENSION, "JIRA", JIRAExtension.EXTENSION, extensionManager);
        this.jiraConfigurationService = jiraConfigurationService;
        this.propertiesService = propertiesService;
        this.transactionService = transactionService;
        this.jiraSessionFactory = jiraSessionFactory;
    }

    @Override
    public IssueServiceConfig getConfigurationById(int id) {
        return jiraConfigurationService.getConfigurationById(id);
    }

    @Override
    public Collection<IssueServiceConfigSummary> getAllConfigurations() {
        return Collections2.transform(
                jiraConfigurationService.getAllConfigurations(),
                IssueServiceConfig.summaryFn
        );
    }

    @Override
    public Set<String> extractIssueKeysFromMessage(IssueServiceConfig issueServiceConfig, String message) {
        return extractJIRAIssuesFromMessage((JIRAConfiguration) issueServiceConfig, message);
    }

    @Override
    public Set<String> extractIssueKeysFromMessage(int projectId, String message) {
        return extractJIRAIssuesFromMessage(getConfigurationForProject(projectId), message);
    }

    protected Set<String> extractJIRAIssuesFromMessage(JIRAConfiguration configuration, String message) {
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
    public String formatIssuesInMessage(IssueServiceConfig issueServiceConfig, String message) {
        JIRAConfiguration configuration = (JIRAConfiguration) issueServiceConfig;
        // First, makes the message HTML-ready
        String htmlMessage = StringEscapeUtils.escapeHtml4(message);
        // Replaces each issue by a link
        StringBuffer html = new StringBuffer();
        Matcher matcher = JIRAConfiguration.ISSUE_PATTERN.matcher(htmlMessage);
        while (matcher.find()) {
            String key = matcher.group();
            if (configuration.isIssue(key)) {
                String href = getIssueURL(configuration, key);
                String link = format("<a href=\"%s\">%s</a>", href, key);
                matcher.appendReplacement(html, link);
            }
        }
        matcher.appendTail(html);
        // OK
        return html.toString();
    }

    @Override
    public net.ontrack.extension.issue.Issue getIssue(IssueServiceConfig issueServiceConfig, String key) {
        return getIssue((JIRAConfiguration) issueServiceConfig, key);
    }

    @Override
    public String getLinkForAllIssues(IssueServiceConfig issueServiceConfig, Collection<net.ontrack.extension.issue.Issue> issues) {
        Validate.notNull(issueServiceConfig, "The issue service configuration is required");
        Validate.notNull(issues, "The list of issues must not be null");
        JIRAConfiguration configuration = (JIRAConfiguration) issueServiceConfig;
        if (issues.size() == 0) {
            // Nothing to link to
            return "";
        } else if (issues.size() == 1) {
            // Link to one issue
            return format(
                    "%s/browse/%s",
                    configuration.getUrl(),
                    issues.iterator().next().getKey()
            );
        } else {
            try {
                return format(
                        "%s/secure/IssueNavigator.jspa?reset=true&mode=hide&jqlQuery=%s",
                        configuration.getUrl(),
                        URLEncoder.encode(
                                format(
                                        "key in (%s)",
                                        StringUtils.join(
                                                Collections2.transform(
                                                        issues,
                                                        new Function<net.ontrack.extension.issue.Issue, String>() {
                                                            @Override
                                                            public String apply(net.ontrack.extension.issue.Issue i) {
                                                                return format("\"%s\"", i.getKey());
                                                            }
                                                        }
                                                ),
                                                ","
                                        )
                                ),
                                "UTF-8"
                        )
                );
            } catch (UnsupportedEncodingException e) {
                throw new IllegalStateException("UTF-8 not supported");
            }
        }
    }

    @Override
    public boolean isIssue(String token) {
        return token != null && JIRAConfiguration.ISSUE_PATTERN.matcher(token).matches();
    }

    @Override
    public String getIssueURL(JIRAConfiguration configuration, String key) {
        return configuration.getIssueURL(key);
    }

    @Override
    public JIRAIssue getIssue(JIRAConfiguration configuration, String key) {
        try (Transaction tx = transactionService.start()) {
            JIRASession session = getJIRASession(tx, configuration);
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
                JIRAStatus status = toStatus(configuration, issue.getStatus());

                // Formatted JIRA issue
                return new JIRAIssue(
                        getIssueURL(configuration, issue.getKey()),
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

    private JIRASession getJIRASession(Transaction tx, final JIRAConfiguration configuration) {
        return tx.getResource(JIRASession.class, configuration.getId(), new TransactionResourceProvider<JIRASession>() {
            @Override
            public JIRASession createTxResource() {
                return jiraSessionFactory.create(configuration);
            }
        });
    }

    @Override
    public boolean isIssue(JIRAConfiguration configuration, String token) {
        return configuration.isIssue(token);
    }

    @Override
    public String getJIRAURL(int projectId) {
        return getConfigurationForProject(projectId).getUrl();
    }

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

    private JIRAStatus toStatus(JIRAConfiguration configuration, BasicStatus status) {
        return new JIRAStatus(
                status.getName(),
                getStatusIconURL(configuration, status)
        );
    }

    private String getStatusIconURL(JIRAConfiguration configuration, BasicStatus status) {
        try (Transaction tx = transactionService.start()) {
            Status s = getJIRASession(tx, configuration).getClient().getMetadataClient().getStatus(status.getSelf()).claim();
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
