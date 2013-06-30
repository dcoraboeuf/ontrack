package net.ontrack.extension.github.model;

import lombok.Data;
import net.ontrack.core.model.ProjectSummary;
import net.ontrack.extension.git.model.GitCommitInfo;

import java.util.List;

@Data
public class GitHubIssueInfo {

    private final ProjectSummary project;
    private final GitHubIssue issue;
    private final GitCommitInfo commitInfo;
    private final List<GitHubCommit> commits;

}
