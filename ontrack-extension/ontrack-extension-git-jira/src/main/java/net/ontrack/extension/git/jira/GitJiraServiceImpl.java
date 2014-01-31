package net.ontrack.extension.git.jira;

import com.google.common.base.Function;
import net.ontrack.core.model.BranchSummary;
import net.ontrack.core.model.ProjectSummary;
import net.ontrack.extension.git.model.GitConfiguration;
import net.ontrack.extension.git.service.GitService;
import net.ontrack.extension.jira.JIRAService;
import net.ontrack.service.ManagementService;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@Service
public class GitJiraServiceImpl implements GitJiraService {

    private final ManagementService managementService;
    private final GitService gitService;
    private final JIRAService jiraService;

    @Autowired
    public GitJiraServiceImpl(ManagementService managementService, GitService gitService, JIRAService jiraService) {
        this.managementService = managementService;
        this.gitService = gitService;
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

}
