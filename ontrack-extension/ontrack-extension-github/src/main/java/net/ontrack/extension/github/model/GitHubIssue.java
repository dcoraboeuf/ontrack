package net.ontrack.extension.github.model;

import lombok.Data;
import org.joda.time.DateTime;

import java.util.List;

@Data
public class GitHubIssue {

    private final int id;
    private final String url;
    private final String title;
    private final String body;
    private final String bodyHtml;
    private final GitHubUser assignee;
    private final List<GitHubLabel> labels;
    private final GitHubState state;
    private final GitHubMilestone milestone;
    private final DateTime createdAt;
    private final DateTime updatedAt;
    private final DateTime closedAt;

}
