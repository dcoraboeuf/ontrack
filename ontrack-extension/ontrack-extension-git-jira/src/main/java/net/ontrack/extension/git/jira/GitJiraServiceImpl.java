package net.ontrack.extension.git.jira;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.ontrack.core.model.BranchSummary;
import net.ontrack.core.model.ProjectSummary;
import net.ontrack.extension.git.client.GitClient;
import net.ontrack.extension.git.client.GitClientFactory;
import net.ontrack.extension.git.client.GitCommit;
import net.ontrack.extension.git.model.GitCommitInfo;
import net.ontrack.extension.git.model.GitConfiguration;
import net.ontrack.extension.git.service.GitService;
import net.ontrack.extension.jira.JIRAService;
import net.ontrack.extension.jira.service.model.JIRAIssue;
import net.ontrack.service.ManagementService;
import org.eclipse.jgit.revwalk.RevCommit;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GitJiraServiceImpl implements GitJiraService {

    private final DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    private final ManagementService managementService;
    private final GitService gitService;
    private final GitClientFactory gitClientFactory;
    private final JIRAService jiraService;

    @Autowired
    public GitJiraServiceImpl(ManagementService managementService, GitService gitService, GitClientFactory gitClientFactory, JIRAService jiraService) {
        this.managementService = managementService;
        this.gitService = gitService;
        this.gitClientFactory = gitClientFactory;
        this.jiraService = jiraService;
    }

    @Override
    public Collection<BranchSummary> getBranchesWithIssue(final String key) {
        final Collection<BranchSummary> result = new ArrayList<>();
        for (ProjectSummary projectSummary : managementService.getProjectList()) {
            int projectId = projectSummary.getId();
            for (final BranchSummary branchSummary : managementService.getBranchList(projectId)) {
                GitConfiguration gitConfiguration = gitService.getGitConfiguration(branchSummary.getId());
                if (gitConfiguration.isValid()) {
                    gitService.scanCommits(branchSummary.getId(), new Function<RevCommit, Boolean>() {
                        @Override
                        public Boolean apply(RevCommit commit) {
                            String message = commit.getFullMessage();
                            Set<String> keys = jiraService.extractIssueKeysFromMessage(message);
                            if (keys.contains(key)) {
                                // Found a branch whose history contains the issue
                                result.add(branchSummary);
                                // No need to scan further
                                return Boolean.TRUE;
                            } else {
                                return Boolean.FALSE;
                            }
                        }
                    });
                }
            }
        }
        return result;
    }

    @Override
    public GitJiraIssueInfo getIssueInfo(Locale locale, int branchId, final String key) {
        // The branch
        BranchSummary branch = managementService.getBranch(branchId);
        // Gets the details about the issue
        JIRAIssue issue = jiraService.getIssue(key);
        // Git configuration
        GitConfiguration gitConfiguration = gitService.getGitConfiguration(branchId);
        // Git client
        final GitClient gitClient = gitClientFactory.getClient(gitConfiguration);
        // Looking for all commits that contains this issue
        final List<RevCommit> revCommits = new ArrayList<>();
        gitService.scanCommits(branchId, new Function<RevCommit, Boolean>() {
            @Override
            public Boolean apply(RevCommit commit) {
                String message = commit.getFullMessage();
                Set<String> keys = jiraService.extractIssueKeysFromMessage(message);
                if (keys.contains(key)) {
                    revCommits.add(commit);
                }
                // Going on
                return Boolean.FALSE;
            }
        });
        // Getting the commit format
        List<GitCommit> commits = Lists.transform(
                revCommits,
                new Function<RevCommit, GitCommit>() {
                    @Override
                    public GitCommit apply(RevCommit revCommit) {
                        return gitClient.toCommit(revCommit);
                    }
                }
        );
        // Getting the last commit complete information
        GitCommitInfo commitInfo = null;
        if (!commits.isEmpty()) {
            GitCommit lastCommit = commits.get(0);
            commitInfo = gitService.getCommitInfo(locale, lastCommit.getId());
        }
        // OK
        return new GitJiraIssueInfo(
                branch,
                issue,
                format.print(issue.getUpdateTime()),
                commitInfo,
                commits
        );
    }

}
