package net.ontrack.extension.git.jira;

import lombok.Data;
import net.ontrack.core.model.BranchSummary;
import net.ontrack.extension.git.client.GitCommit;
import net.ontrack.extension.git.model.GitCommitInfo;
import net.ontrack.extension.jira.service.model.JIRAIssue;

import java.util.List;

@Data
public class GitJiraIssueInfo {

    private final BranchSummary branch;
    private final JIRAIssue issue;
    private final String formattedUpdateTime;
    private final GitCommitInfo commitInfo;
    private final List<GitCommit> commits;

}
