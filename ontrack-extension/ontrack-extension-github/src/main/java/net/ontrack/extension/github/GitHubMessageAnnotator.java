package net.ontrack.extension.github;

import com.google.common.base.Function;
import net.ontrack.core.model.BranchSummary;
import net.ontrack.core.support.MessageAnnotation;
import net.ontrack.core.support.MessageAnnotator;
import net.ontrack.core.support.RegexMessageAnnotator;
import net.ontrack.extension.api.ExtensionManager;
import net.ontrack.extension.git.service.GitMessageAnnotator;
import net.ontrack.extension.github.service.GitHubService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GitHubMessageAnnotator implements GitMessageAnnotator {

    private final ExtensionManager extensionManager;
    private final GitHubService gitHubService;

    @Autowired
    public GitHubMessageAnnotator(ExtensionManager extensionManager, GitHubService gitHubService) {
        this.extensionManager = extensionManager;
        this.gitHubService = gitHubService;
    }

    @Override
    public MessageAnnotator annotator(BranchSummary branch) {
        if (extensionManager.isExtensionEnabled(GitHubExtension.EXTENSION)) {
            final String project = gitHubService.getGitHubProject(branch.getProject().getId());
            if (StringUtils.isNotBlank(project)) {
                return new RegexMessageAnnotator(
                        GitHubExtension.GITHUB_ISSUE_PATTERN,
                        new Function<String, MessageAnnotation>() {
                            @Override
                            public MessageAnnotation apply(String token) {
                                String id = token.substring(1);
                                return MessageAnnotation.of("a")
                                        .attr("href", GitHubExtension.getIssueUrl(project, id))
                                        .text(token);
                            }
                        }
                );
            }
        }
        return null;
    }
}
