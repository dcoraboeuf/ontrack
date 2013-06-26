package net.ontrack.extension.github;

import net.ontrack.core.model.BranchSummary;
import net.ontrack.extension.api.ExtensionManager;
import net.ontrack.extension.git.model.GitConfiguration;
import net.ontrack.extension.git.service.GitConfigurator;
import net.ontrack.extension.github.service.GitHubService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.lang.String.format;

@Component
public class GitHubConfigurator implements GitConfigurator {

    private final ExtensionManager extensionManager;
    private final GitHubService gitHubService;

    @Autowired
    public GitHubConfigurator(ExtensionManager extensionManager, GitHubService gitHubService) {
        this.extensionManager = extensionManager;
        this.gitHubService = gitHubService;
    }

    @Override
    public GitConfiguration configure(GitConfiguration configuration, BranchSummary branch) {
        if (extensionManager.isExtensionEnabled(GitHubExtension.EXTENSION)) {
            String project = gitHubService.getGitHubProject(branch.getProject().getId());
            if (StringUtils.isNotBlank(project)) {
                return configuration
                        .withRemote(format("https://github.com/%s.git", project))
                        .withCommitLink(format("https://github.com/%s/commit/*", project))
                        .withFileAtCommitLink(format("https://github.com/%s/blob/*/$", project));
            } else {
                return configuration;
            }
        } else {
            return configuration;
        }
    }
}
