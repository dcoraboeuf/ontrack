package net.ontrack.extension.github.client;

import net.ontrack.extension.github.model.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class DefaultGitHubClientTest {

    @Test
    public void getIssue() {
        GitHubClient client = new DefaultGitHubClient();
        GitHubIssue issue = client.getIssue("dcoraboeuf/ontrack", 2);
        assertEquals(
                new GitHubIssue(
                        2,
                        "Management of projects",
                        new GitHubUser("dcoraboeuf"),
                        Arrays.asList(new GitHubLabel("feature", "0b02e1")),
                        GitHubState.closed,
                        new GitHubMilestone(
                                "0.1",
                                GitHubState.closed
                        ),
                        new DateTime(2013, 1, 25, 20, 32, 29, DateTimeZone.UTC),
                        new DateTime(2013, 4, 17, 18, 48, 53, DateTimeZone.UTC),
                        new DateTime(2013, 4, 17, 18, 48, 53, DateTimeZone.UTC)
                ),
                issue);
    }

}
