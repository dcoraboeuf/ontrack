package net.ontrack.extension.git.jira;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import net.ontrack.core.model.BranchSummary;
import net.ontrack.core.model.SearchResult;
import net.ontrack.extension.api.ExtensionManager;
import net.ontrack.extension.jira.service.model.JIRAConfiguration;
import net.ontrack.service.GUIService;
import net.ontrack.service.SearchProvider;
import net.ontrack.web.support.AbstractGUIController;
import net.ontrack.web.support.EntityConverter;
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

@Controller
public class GitJiraIssueSearchProvider extends AbstractGUIController implements SearchProvider {

    private final ExtensionManager extensionManager;
    private final GUIService guiService;
    private final GitJiraService gitJiraService;
    private final EntityConverter entityConverter;

    @Autowired
    public GitJiraIssueSearchProvider(ErrorHandler errorHandler, ExtensionManager extensionManager, GUIService guiService, GitJiraService gitJiraService, EntityConverter entityConverter) {
        super(errorHandler);
        this.extensionManager = extensionManager;
        this.guiService = guiService;
        this.gitJiraService = gitJiraService;
        this.entityConverter = entityConverter;
    }

    @Override
    public boolean isTokenSearchable(String token) {
        return JIRAConfiguration.ISSUE_PATTERN.matcher(token).matches();
    }

    @Override
    public Collection<SearchResult> search(final String key) {
        if (extensionManager.isExtensionEnabled(GitJiraExtension.EXTENSION)) {
            Collection<BranchSummary> branches = gitJiraService.getBranchesWithIssue(key);
            return Collections2.transform(
                    branches,
                    new Function<BranchSummary, SearchResult>() {
                        @Override
                        public SearchResult apply(BranchSummary branch) {
                            return new SearchResult(
                                    String.format("%s / %s/ %s", branch.getProject().getName(), branch.getName(), key),
                                    new LocalizableMessage("git-jira.search.issue", key, branch.getProject().getName(), branch.getName()),
                                    guiService.toGUI(String.format("extension/git-jira/issue/%s/%s/%s", branch.getProject().getName(), branch.getName(), key)),
                                    80
                            );
                        }
                    }
            );
        } else {
            return Collections.emptyList();
        }
    }

    @RequestMapping(value = "/gui/extension/git-jira/issue/{project:[A-Za-z0-9_\\.\\-]+}/{branch:[A-Za-z0-9_\\.\\-]+}/{key:[A-Z]+-\\d+}", method = RequestMethod.GET)
    public String issue(Locale locale, @PathVariable String project, @PathVariable String branch, @PathVariable String key, Model model) {
        // Gets the branch id
        int branchId = entityConverter.getBranchId(project, branch);
        // Issue info
        model.addAttribute("info", gitJiraService.getIssueInfo(locale, branchId, key));
        // OK
        return "extension/git-jira/issue";
    }

}
