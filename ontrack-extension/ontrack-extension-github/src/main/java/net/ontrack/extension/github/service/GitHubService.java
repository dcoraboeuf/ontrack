package net.ontrack.extension.github.service;

import net.ontrack.core.model.ProjectSummary;
import net.ontrack.extension.git.client.GitCommit;
import net.ontrack.extension.github.model.GitHubIssue;

import java.util.Collection;
import java.util.List;

public interface GitHubService {

    String getGitHubProject(int projectId);

    List<GitHubIssue> getGitHubIssues(int branchId, List<GitCommit> commits);

    Collection<ProjectSummary> getProjectsWithIssue(int issue);
}
