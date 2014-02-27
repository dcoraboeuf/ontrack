package net.ontrack.extension.svnexplorer;

import net.ontrack.core.model.SearchResult;
import net.ontrack.extension.api.ExtensionManager;
import net.ontrack.extension.issue.IssueService;
import net.ontrack.extension.issue.IssueServiceConfigSummary;
import net.ontrack.extension.issue.IssueServiceFactory;
import net.ontrack.extension.issue.IssueServiceSummary;
import net.ontrack.extension.svn.service.RepositoryService;
import net.ontrack.extension.svn.service.SubversionService;
import net.ontrack.extension.svn.service.model.SVNRepository;
import net.ontrack.service.GUIService;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.*;
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

    @Test
    public void search_jira() {
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

        // Extension manager
        ExtensionManager extensionManager = mock(ExtensionManager.class);
        when(extensionManager.isExtensionEnabled(SVNExplorerExtension.EXTENSION)).thenReturn(true);

        // Repositories
        SVNRepository svnJira = new SVNRepository(1, "svn-jira", "svn://svn/jira", "user", "", "", "", "", "", "", 0, 1, new IssueServiceSummary("jira", "JIRA"), new IssueServiceConfigSummary(1, "JIRA"));
        RepositoryService repositoryService = mock(RepositoryService.class);
        when(repositoryService.getAllRepositories()).thenReturn(Arrays.asList(
                svnJira
        ));
        SubversionService subversionService = mock(SubversionService.class);
        when(subversionService.isIndexedIssue(svnJira, "PRJ-1")).thenReturn(true);

        // GUI service
        GUIService guiService = mock(GUIService.class);
        when(guiService.toGUI("extension/svnexplorer/repository/1/issue/PRJ-1")).thenReturn("http://ontrack/extension/svnexplorer/repository/1/issue/PRJ-1");

        // Service
        IssueSearchProvider provider = new IssueSearchProvider(
                repositoryService,
                subversionService,
                guiService,
                issueServiceFactory,
                extensionManager
        );

        // Search
        Collection<SearchResult> results = provider.search("PRJ-1");
        assertNotNull(results);
        // FIXME Equality between Localizables (jstring)
//        assertEquals(
//                Arrays.asList(
//                        new SearchResult(
//                                "PRJ-1",
//                                new LocalizableMessage("svnexplorer.search.key", "PRJ-1", "svn-jira"),
//                                "http://ontrack/extension/svnexplorer/repository/1/issue/PRJ-1",
//                                80
//                        )
//                ),
//                results
//        );
    }

}
