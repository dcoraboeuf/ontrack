package net.ontrack.extension.jira.service;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.util.ErrorCollection;
import net.ontrack.extension.jira.JIRAConfigurationExtension;
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
        JIRAConfigurationExtension configurationExtension = mock(JIRAConfigurationExtension.class);
        TransactionService transactionService = mock(TransactionService.class);
        Transaction tx = mock(Transaction.class);
        JIRASession jiraSession = mock(JIRASession.class);
        JiraRestClient client = mock(JiraRestClient.class);
        IssueRestClient issueClient = mock(IssueRestClient.class);

        when(transactionService.start()).thenReturn(tx);
        when(tx.getResource(JIRASession.class)).thenReturn(jiraSession);
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
                configurationExtension,
                transactionService
        );
        JIRAIssue issue = service.getIssue("XXX-1");
        assertNull(issue);
    }

    @Test
    public void isIssue() {
        JIRAConfigurationExtension configurationExtension = mock(JIRAConfigurationExtension.class);
        TransactionService transactionService = mock(TransactionService.class);
        DefaultJIRAService service = new DefaultJIRAService(
                configurationExtension,
                transactionService
        );
        service.isIssue("TEST-12");
        verify(configurationExtension, times(1)).isIssue("TEST-12");
    }

    @Test
    public void insertIssueUrlsInMessage() {
        JIRAConfigurationExtension configurationExtension = mock(JIRAConfigurationExtension.class);
        when(configurationExtension.isIssue("PRJ-13")).thenReturn(true);
        when(configurationExtension.getIssueURL("PRJ-13")).thenReturn("http://jira/browse/PRJ-13");
        TransactionService transactionService = mock(TransactionService.class);
        DefaultJIRAService service = new DefaultJIRAService(
                configurationExtension,
                transactionService
        );
        String message = service.insertIssueUrlsInMessage("TEST-12, PRJ-12, PRJ-13 List of issues");
        assertEquals("TEST-12, PRJ-12, <a href=\"http://jira/browse/PRJ-13\">PRJ-13</a> List of issues", message);
    }

    @Test
    public void extractIssueKeysFromMessage() {
        JIRAConfigurationExtension configurationExtension = mock(JIRAConfigurationExtension.class);
        when(configurationExtension.isIssue("PRJ-13")).thenReturn(true);
        TransactionService transactionService = mock(TransactionService.class);
        DefaultJIRAService service = new DefaultJIRAService(
                configurationExtension,
                transactionService
        );
        Set<String> issues = service.extractIssueKeysFromMessage("TEST-12, PRJ-12, PRJ-13 List of issues");
        assertEquals(Collections.singleton("PRJ-13"), issues);
    }

}
