package net.ontrack.extension.github;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import net.ontrack.core.model.ProjectSummary;
import net.ontrack.core.model.SearchResult;
import net.ontrack.extension.api.ExtensionManager;
import net.ontrack.extension.github.service.GitHubService;
import net.ontrack.service.GUIService;
import net.ontrack.service.SearchProvider;
import net.ontrack.web.support.AbstractGUIController;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.LocalizableMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.regex.Pattern;

@Controller
public class GitHubIssueSearchProvider extends AbstractGUIController implements SearchProvider {

    private final ExtensionManager extensionManager;
    private final GitHubService gitHubService;
    private final GUIService guiService;

    @Autowired
    public GitHubIssueSearchProvider(ErrorHandler errorHandler, ExtensionManager extensionManager, GitHubService gitHubService, GUIService guiService) {
        super(errorHandler);
        this.extensionManager = extensionManager;
        this.gitHubService = gitHubService;
        this.guiService = guiService;
    }

    @Override
    public boolean isTokenSearchable(String token) {
        return Pattern.matches(GitHubExtension.GITHUB_ISSUE_PATTERN, token);
    }

    @Override
    public Collection<SearchResult> search(final String token) {
        if (extensionManager.isExtensionEnabled(GitHubExtension.EXTENSION)) {
            final int issue = Integer.parseInt(token.substring(1), 10);
            Collection<ProjectSummary> projects = gitHubService.getProjectsWithIssue(issue);
            return Collections2.transform(
                    projects,
                    new Function<ProjectSummary, SearchResult>() {
                        @Override
                        public SearchResult apply(ProjectSummary project) {
                            return new SearchResult(
                                    token,
                                    new LocalizableMessage("github.search.issue", issue, project.getName()),
                                    guiService.toGUI(String.format("extension/github/project/%s/issue/%s", project.getName(), issue))
                            );
                        }
                    }
            );
        } else {
            return Collections.emptyList();
        }
    }

    @RequestMapping(value = "/gui/extension/github/project/{project:[A-Za-z0-9_\\.\\-]+}/issue/{issue:\\d+}", method = RequestMethod.GET)
    public String commit(Locale locale, @PathVariable String project, @PathVariable String issue, Model model) {
        // Issue info
        // TODO model.addAttribute("commit", gitUI.getCommitInfo(locale, commit));
        // OK
        return "extension/github/issue";
    }

}
