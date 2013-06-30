package net.ontrack.extension.github.service;

import net.ontrack.core.model.ProjectSummary;
import net.ontrack.extension.git.client.GitCommit;
import net.ontrack.extension.github.model.GitHubIssue;
import net.ontrack.extension.github.model.GitHubIssueInfo;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

public interface GitHubService {

    String getGitHubProject(int projectId);

    List<GitHubIssue> getGitHubIssues(int branchId, List<GitCommit> commits);

    Collection<ProjectSummary> getProjectsWithIssue(int issue);

    GitHubIssueInfo getIssueInfo(Locale locale, int projectId, int issue);
}
