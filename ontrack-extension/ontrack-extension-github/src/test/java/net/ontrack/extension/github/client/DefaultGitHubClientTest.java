package net.ontrack.extension.github.client;

import net.ontrack.extension.github.model.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DefaultGitHubClientTest {

    @Test
    public void getIssue_no_body() {
        OntrackGitHubClient client = new DefaultOntrackGitHubClient();
        GitHubIssue issue = client.getIssue("dcoraboeuf/ontrack", Mockito.mock(GitHubClientConfigurator.class), 2);
        assertEquals(
                new GitHubIssue(
                        2,
                        "https://github.com/dcoraboeuf/ontrack/issues/2",
                        "Management of projects",
                        "",
                        "",
                        new GitHubUser("dcoraboeuf", "https://github.com/dcoraboeuf"),
                        Arrays.asList(new GitHubLabel("feature", "0b02e1")),
                        GitHubState.closed,
                        new GitHubMilestone(
                                "0.1",
                                GitHubState.closed,
                                1,
                                "https://github.com/dcoraboeuf/ontrack/issues?milestone=1&state=open"
                        ),
                        new DateTime(2013, 1, 25, 20, 32, 29, DateTimeZone.UTC),
                        new DateTime(2013, 4, 17, 18, 48, 53, DateTimeZone.UTC),
                        new DateTime(2013, 4, 17, 18, 48, 53, DateTimeZone.UTC)
                ),
                issue);
    }

    @Test
    public void getIssue_body() {
        OntrackGitHubClient client = new DefaultOntrackGitHubClient();
        GitHubIssue issue = client.getIssue("dcoraboeuf/ontrack", Mockito.mock(GitHubClientConfigurator.class), 172);
        assertEquals(
                new GitHubIssue(
                        172,
                        "https://github.com/dcoraboeuf/ontrack/issues/172",
                        "Management of extensions",
                        "Extensions can be enabled/disabled.\n" +
                                "Extensions have dependencies",
                        "<ul class=\"task-list\">\n" +
                                "<li>Extensions can be enabled/disabled.</li>\n" +
                                "<li>Extensions have dependencies</li>\n" +
                                "</ul>",
                        new GitHubUser("dcoraboeuf", "https://github.com/dcoraboeuf"),
                        Arrays.asList(
                                new GitHubLabel("branch", "02d7e1"),
                                new GitHubLabel("extension", "e102d8")
                        ),
                        GitHubState.closed,
                        new GitHubMilestone(
                                "1.21",
                                GitHubState.closed,
                                24,
                                "https://github.com/dcoraboeuf/ontrack/issues?milestone=24&state=open"
                        ),
                        // createdAt=2013-06-08T10:14:36.000Z, updatedAt=2013-06-14T12:08:24.000Z, closedAt=2013-06-14T12:08:24.000Z)
                        new DateTime(2013, 6, 8, 10, 14, 36, DateTimeZone.UTC),
                        new DateTime(2013, 6, 14, 12, 8, 24, DateTimeZone.UTC),
                        new DateTime(2013, 6, 14, 12, 8, 24, DateTimeZone.UTC)
                ),
                issue);
    }

    @Test
    public void getIssue_not_found() {
        OntrackGitHubClient client = new DefaultOntrackGitHubClient();
        GitHubIssue issue = client.getIssue("dcoraboeuf/ontrack", Mockito.mock(GitHubClientConfigurator.class), 98000);
        assertNull(issue);
    }

}
