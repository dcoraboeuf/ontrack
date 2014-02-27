package net.ontrack.extension.svnexplorer;

import net.ontrack.extension.issue.IssueService;
import net.ontrack.extension.issue.IssueServiceFactory;
import net.ontrack.extension.issue.IssueServiceSummary;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IssueSearchProviderTest {

    @Test
    public void isTokenSearchable_one() {
        // Issue service mock
        IssueService jiraIssueService = mock(IssueService.class);
        when(jiraIssueService.isIssue("PRJ-1")).thenReturn(true);
        IssueServiceFactory issueServiceFactory = mock(IssueServiceFactory.class);
        when(issueServiceFactory.getAllServices()).thenReturn(Collections.singleton(new IssueServiceSummary("jira", "JIRA service")));
        when(issueServiceFactory.getServiceByName("jira")).thenReturn(jiraIssueService);
        // Service
        IssueSearchProvider provider = new IssueSearchProvider(
                null,
                null,
                null,
                issueServiceFactory,
                null
        );
        // Token search
        assertTrue(provider.isTokenSearchable("PRJ-1"));
        assertFalse(provider.isTokenSearchable("#123"));
    }

    @Test
    public void isTokenSearchable_two() {
        // Issue service mock
        IssueService jiraIssueService = mock(IssueService.class);
        when(jiraIssueService.isIssue("PRJ-1")).thenReturn(true);
        IssueService githubIssueService = mock(IssueService.class);
        when(githubIssueService.isIssue("#123")).thenReturn(true);
        IssueServiceFactory issueServiceFactory = mock(IssueServiceFactory.class);
        when(issueServiceFactory.getAllServices()).thenReturn(Arrays.asList(
                new IssueServiceSummary("jira", "JIRA service"),
                new IssueServiceSummary("github", "GitHub service")
        ));
        when(issueServiceFactory.getServiceByName("jira")).thenReturn(jiraIssueService);
        when(issueServiceFactory.getServiceByName("github")).thenReturn(githubIssueService);
        // Service
        IssueSearchProvider provider = new IssueSearchProvider(
                null,
                null,
                null,
                issueServiceFactory,
                null
        );
        // Token search
        assertTrue(provider.isTokenSearchable("PRJ-1"));
        assertTrue(provider.isTokenSearchable("#123"));
    }

}
