package net.ontrack.extension.github.client;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.ontrack.extension.github.model.*;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Label;
import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.RequestException;
import org.eclipse.egit.github.core.service.IssueService;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Component
public class DefaultOntrackGitHubClient implements OntrackGitHubClient {

    @Override
    public GitHubIssue getIssue(String project, int id) {
        // GitHub client (non authentified)
        GitHubClient client = new GitHubClient();
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
                issue.getTitle(),
                toUser(issue.getAssignee()),
                toLabels(issue.getLabels()),
                toState(issue.getState()),
                toMilestone(issue.getMilestone()),
                toDateTime(issue.getCreatedAt()),
                toDateTime(issue.getUpdatedAt()),
                toDateTime(issue.getClosedAt())
        );
    }

    private DateTime toDateTime(Date date) {
        if (date == null) {
            return null;
        } else {
            return new DateTime(date, DateTimeZone.UTC);
        }
    }

    private GitHubMilestone toMilestone(Milestone milestone) {
        return new GitHubMilestone(
                milestone.getTitle(),
                toState(milestone.getState())
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
                    user.getLogin()
            );
        }
    }

}
