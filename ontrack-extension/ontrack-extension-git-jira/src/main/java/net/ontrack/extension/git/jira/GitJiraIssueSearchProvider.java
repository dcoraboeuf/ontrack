package net.ontrack.extension.git.jira;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import net.ontrack.core.model.BranchSummary;
import net.ontrack.core.model.ProjectSummary;
import net.ontrack.core.model.SearchResult;
import net.ontrack.extension.api.ExtensionManager;
import net.ontrack.extension.jira.JIRAService;
import net.ontrack.service.GUIService;
import net.ontrack.service.SearchProvider;
import net.sf.jstring.LocalizableMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.Collections;

@Controller
public class GitJiraIssueSearchProvider implements SearchProvider {

    private final JIRAService jiraService;
    private final ExtensionManager extensionManager;
    private final GUIService guiService;
    private final GitJiraService gitJiraService;

    @Autowired
    public GitJiraIssueSearchProvider(JIRAService jiraService, ExtensionManager extensionManager, GUIService guiService, GitJiraService gitJiraService) {
        this.jiraService = jiraService;
        this.extensionManager = extensionManager;
        this.guiService = guiService;
        this.gitJiraService = gitJiraService;
    }

    @Override
    public boolean isTokenSearchable(String token) {
        return jiraService.isIssue(token);
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

}
