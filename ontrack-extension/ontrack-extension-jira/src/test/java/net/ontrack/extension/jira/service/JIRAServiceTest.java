package net.ontrack.extension.jira.service;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.util.ErrorCollection;
import net.ontrack.extension.api.property.PropertiesService;
import net.ontrack.extension.jira.JIRAConfigurationExtension;
import net.ontrack.extension.jira.JIRAConfigurationService;
import net.ontrack.extension.jira.service.model.JIRAConfiguration;
import net.ontrack.extension.jira.service.model.JIRAIssue;
import net.ontrack.extension.jira.tx.JIRASession;
import net.ontrack.tx.Transaction;
import net.ontrack.tx.TransactionService;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

public class JIRAServiceTest {

    @Test
    public void issueNotFound() {
        JIRAConfiguration config = createJiraConfiguration();
        TransactionService transactionService = mock(TransactionService.class);
        Transaction tx = mock(Transaction.class);
        JIRASession jiraSession = mock(JIRASession.class);
        JiraRestClient client = mock(JiraRestClient.class);
        IssueRestClient issueClient = mock(IssueRestClient.class);
        JIRAConfigurationService jiraConfigurationService = mock(JIRAConfigurationService.class);
        PropertiesService propertiesService = mock(PropertiesService.class);

        when(transactionService.start()).thenReturn(tx);
        // TODO when(tx.getResource(JIRASession.class)).thenReturn(jiraSession);
        when(jiraSession.getClient()).thenReturn(client);
        when(client.getIssueClient()).thenReturn(issueClient);
        when(issueClient.getIssue("XXX-1")).thenThrow(new RestClientException(
                Arrays.asList(
                        new ErrorCollection(
                                404,
                                Arrays.asList("Issue Does Not Exist"),
                                Collections.<String, String>emptyMap()
                        )
                ),
                404
        ));

        DefaultJIRAService service = new DefaultJIRAService(
                jiraConfigurationService, propertiesService, transactionService
        );
        JIRAIssue issue = service.getIssue(config, "XXX-1");
        assertNull(issue);
    }

    private JIRAConfiguration createJiraConfiguration() {
        return new JIRAConfiguration(0, "test", "http://jira", "user", "pwd", Collections.<String>emptySet(), Collections.<String>emptySet());
    }

    @Test
    public void isIssue() {
        JIRAConfiguration config = createJiraConfiguration();
        JIRAConfigurationExtension configurationExtension = mock(JIRAConfigurationExtension.class);
        JIRAConfigurationService jiraConfigurationService = mock(JIRAConfigurationService.class);
        PropertiesService propertiesService = mock(PropertiesService.class);
        TransactionService transactionService = mock(TransactionService.class);
        DefaultJIRAService service = new DefaultJIRAService(
                jiraConfigurationService, propertiesService, transactionService
        );
        service.isIssue(config, "TEST-12");
        verify(configurationExtension, times(1)).isIssue("TEST-12");
    }

    @Test
    public void insertIssueUrlsInMessage() {
        JIRAConfiguration config = createJiraConfiguration();
        JIRAConfigurationExtension configurationExtension = mock(JIRAConfigurationExtension.class);
        JIRAConfigurationService jiraConfigurationService = mock(JIRAConfigurationService.class);
        PropertiesService propertiesService = mock(PropertiesService.class);
        when(configurationExtension.isIssue("PRJ-13")).thenReturn(true);
        when(configurationExtension.getIssueURL("PRJ-13")).thenReturn("http://jira/browse/PRJ-13");
        TransactionService transactionService = mock(TransactionService.class);
        DefaultJIRAService service = new DefaultJIRAService(
                jiraConfigurationService, propertiesService, transactionService
        );
        String message = service.insertIssueUrlsInMessage(config, "TEST-12, PRJ-12, PRJ-13 List of issues");
        assertEquals("TEST-12, PRJ-12, <a href=\"http://jira/browse/PRJ-13\">PRJ-13</a> List of issues", message);
    }

    @Test
    public void extractIssueKeysFromMessage() {
        JIRAConfigurationExtension configurationExtension = mock(JIRAConfigurationExtension.class);
        when(configurationExtension.isIssue("PRJ-13")).thenReturn(true);
        JIRAConfigurationService jiraConfigurationService = mock(JIRAConfigurationService.class);
        PropertiesService propertiesService = mock(PropertiesService.class);
        TransactionService transactionService = mock(TransactionService.class);
        DefaultJIRAService service = new DefaultJIRAService(
                jiraConfigurationService, propertiesService, transactionService
        );
        Set<String> issues = service.extractIssueKeysFromMessage(1, "TEST-12, PRJ-12, PRJ-13 List of issues");
        assertEquals(Collections.singleton("PRJ-13"), issues);
    }

}
