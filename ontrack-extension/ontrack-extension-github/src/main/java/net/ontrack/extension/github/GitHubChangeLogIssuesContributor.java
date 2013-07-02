package net.ontrack.extension.github;

import com.google.common.collect.Lists;
import net.ontrack.core.model.BranchSummary;
import net.ontrack.extension.api.ExtensionManager;
import net.ontrack.extension.git.GitChangeLogContributor;
import net.ontrack.extension.git.GitChangeLogExtension;
import net.ontrack.extension.git.model.ChangeLogCommits;
import net.ontrack.extension.git.model.GitUICommit;
import net.ontrack.extension.git.ui.GitUI;
import net.ontrack.extension.github.model.GitHubIssue;
import net.ontrack.extension.github.service.GitHubConfigurationService;
import net.ontrack.extension.github.service.GitHubService;
import net.ontrack.web.support.AbstractUIController;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Strings;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Locale;

@Controller
public class GitHubChangeLogIssuesContributor extends AbstractUIController implements GitChangeLogContributor {

    private final ExtensionManager extensionManager;
    private final GitHubService gitHubService;
    private final GitHubConfigurationService gitHubConfigurationService;
    private final GitUI gitUI;

    @Autowired
    public GitHubChangeLogIssuesContributor(ErrorHandler errorHandler, Strings strings, ExtensionManager extensionManager, GitHubService gitHubService, GitHubConfigurationService gitHubConfigurationService, GitUI gitUI) {
        super(errorHandler, strings);
        this.extensionManager = extensionManager;
        this.gitHubService = gitHubService;
        this.gitHubConfigurationService = gitHubConfigurationService;
        this.gitUI = gitUI;
    }

    @Override
    public boolean isApplicable(BranchSummary branch) {
        return extensionManager.isExtensionEnabled(GitHubExtension.EXTENSION) &&
                StringUtils.isNotBlank(gitHubConfigurationService.getGitHubProject(branch.getProject().getId()));
    }

    @Override
    public GitChangeLogExtension getExtension(BranchSummary branch) {
        return new GitChangeLogExtension(
                GitHubExtension.EXTENSION,
                "issues",
                "github.changelog.issues"
        );
    }

    @RequestMapping(value = "/ui/extension/github/issues/{uuid}", method = RequestMethod.GET)
    public
    @ResponseBody
    List<GitHubIssue> issues(Locale locale, @PathVariable String uuid) {
        // Gets the change log
        int branchId = gitUI.getChangeLog(uuid).getSummary().getBranch().getId();
        ChangeLogCommits changeLog = gitUI.getChangeLogCommits(locale, uuid);
        // OK
        return gitHubService.getGitHubIssues(
                branchId,
                Lists.transform(
                        changeLog.getLog().getCommits(),
                        GitUICommit.getCommitFn
                )
        );
    }
}
