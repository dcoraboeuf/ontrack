package net.ontrack.extension.github;

import net.ontrack.core.model.BranchSummary;
import net.ontrack.extension.api.ExtensionManager;
import net.ontrack.extension.git.GitChangeLogContributor;
import net.ontrack.extension.git.GitChangeLogExtension;
import net.ontrack.extension.github.service.GitHubService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GitHubChangeLogIssuesContributor implements GitChangeLogContributor {

    private final ExtensionManager extensionManager;
    private final GitHubService gitHubService;

    @Autowired
    public GitHubChangeLogIssuesContributor(ExtensionManager extensionManager, GitHubService gitHubService) {
        this.extensionManager = extensionManager;
        this.gitHubService = gitHubService;
    }

    @Override
    public boolean isApplicable(BranchSummary branch) {
        return extensionManager.isExtensionEnabled(GitHubExtension.EXTENSION) &&
                StringUtils.isNotBlank(gitHubService.getGitHubProject(branch.getProject().getId()));
    }

    @Override
    public GitChangeLogExtension getExtension(BranchSummary branch) {
        return new GitChangeLogExtension(
                GitHubExtension.EXTENSION,
                "issues",
                "github.changelog.issues"
        );
    }
}
