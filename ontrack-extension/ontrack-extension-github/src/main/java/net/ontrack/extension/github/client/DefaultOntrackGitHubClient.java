package net.ontrack.extension.github.client;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.ontrack.extension.github.model.*;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.egit.github.core.*;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.client.RequestException;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.IssueService;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.*;

@Component
public class DefaultOntrackGitHubClient implements OntrackGitHubClient {

    public static final int ISSUE_COMMITS_MAX_NUMBER = 40;

    @Override
    public GitHubIssue getIssue(String project, GitHubClientConfigurator configurator, int id) {
        // GitHub client (non authentified)
        GitHubClient client = new GitHubClient() {
            @Override
            protected HttpURLConnection configureRequest(HttpURLConnection request) {
                HttpURLConnection connection = super.configureRequest(request);
                connection.setRequestProperty(HEADER_ACCEPT, "application/vnd.github.v3.full+json");
                return connection;
            }
        };
        configurator.configure(client);
        // Issue service using this client
        IssueService service = new IssueService(client);
        // Gets the repository for this project
        String owner = StringUtils.substringBefore(project, "/");
        String name = StringUtils.substringAfter(project, "/");
        Issue issue;
        try {
            issue = service.getIssue(owner, name, id);
        } catch (RequestException ex) {
            if (ex.getStatus() == 404) {
                return null;
            } else {
                throw new OntrackGitHubClientException(ex);
            }
        } catch (IOException e) {
            throw new OntrackGitHubClientException(e);
        }
        // Conversion
        return new GitHubIssue(
                id,
                issue.getHtmlUrl(),
                issue.getTitle(),
                issue.getBodyText(),
                issue.getBodyHtml(),
                toUser(issue.getAssignee()),
                toLabels(issue.getLabels()),
                toState(issue.getState()),
                toMilestone(project, issue.getMilestone()),
                toDateTime(issue.getCreatedAt()),
                toDateTime(issue.getUpdatedAt()),
                toDateTime(issue.getClosedAt())
        );
    }

    @Override
    public List<GitHubCommit> getCommitsForIssue(String project, GitHubClientConfigurator configurator, int id) {
        String owner = StringUtils.substringBefore(project, "/");
        String name = StringUtils.substringAfter(project, "/");
        // GitHub client (non authentified)
        GitHubClient client = new GitHubClient();
        configurator.configure(client);
        // Issue service
        IssueService issueService = new IssueService(client);
        // Commit service
        CommitService commitService = new CommitService(client);
        // Gets the events for the issue
        PageIterator<IssueEvent> eventsIterator = issueService.pageIssueEvents(owner, name, id, ISSUE_COMMITS_MAX_NUMBER);
        if (eventsIterator.hasNext()) {
            List<GitHubCommit> commits = new ArrayList<>();
            Collection<IssueEvent> events = eventsIterator.next();
            for (IssueEvent event : events) {
                String commitId = event.getCommitId();
                if (StringUtils.isNotBlank(commitId)) {
                    try {
                        RepositoryCommit commit = commitService.getCommit(
                                RepositoryId.create(owner, name),
                                commitId
                        );
                        commits.add(new GitHubCommit(
                                commit.getSha(),
                                commit.getCommit().getAuthor().getName(),
                                new DateTime(commit.getCommit().getAuthor().getDate(), DateTimeZone.UTC),
                                commit.getCommit().getMessage()
                        ));
                    } catch (IOException e) {
                        throw new OntrackGitHubClientException(e);
                    }
                }
            }
            return commits;
        } else {
            return Collections.emptyList();
        }
    }

    private DateTime toDateTime(Date date) {
        if (date == null) {
            return null;
        } else {
            return new DateTime(date, DateTimeZone.UTC);
        }
    }

    private GitHubMilestone toMilestone(String project, Milestone milestone) {
        return new GitHubMilestone(
                milestone.getTitle(),
                toState(milestone.getState()),
                milestone.getNumber(),
                String.format("https://github.com/%s/issues?milestone=%d&state=open", project, milestone.getNumber())
        );
    }

    private GitHubState toState(String state) {
        return GitHubState.valueOf(state);
    }

    private List<GitHubLabel> toLabels(List<Label> labels) {
        return Lists.transform(
                labels,
                new Function<Label, GitHubLabel>() {
                    @Override
                    public GitHubLabel apply(Label label) {
                        return new GitHubLabel(
                                label.getName(),
                                label.getColor()
                        );
                    }
                }
        );
    }

    private GitHubUser toUser(User user) {
        if (user == null) {
            return null;
        } else {
            return new GitHubUser(
                    user.getLogin(),
                    user.getHtmlUrl()
            );
        }
    }

}
