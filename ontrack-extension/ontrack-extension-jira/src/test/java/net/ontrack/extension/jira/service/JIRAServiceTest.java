package net.ontrack.extension.jira.service;

import net.ontrack.extension.jira.JIRAConfigurationExtension;
import net.ontrack.tx.TransactionService;
import org.junit.Test;

import java.util.Collections;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class JIRAServiceTest {

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
