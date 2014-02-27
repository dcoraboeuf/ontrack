package net.ontrack.extension.svnexplorer;

import net.ontrack.core.model.SearchResult;
import net.ontrack.extension.api.ExtensionManager;
import net.ontrack.extension.issue.IssueService;
import net.ontrack.extension.issue.IssueServiceFactory;
import net.ontrack.extension.issue.IssueServiceSummary;
import net.ontrack.extension.svn.service.RepositoryService;
import net.ontrack.extension.svn.service.SubversionService;
import net.ontrack.extension.svn.service.model.SVNRepository;
import net.ontrack.service.GUIService;
import net.ontrack.service.SearchProvider;
import net.sf.jstring.LocalizableMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@Component
public class IssueSearchProvider implements SearchProvider {

    private final RepositoryService repositoryService;
    private final SubversionService subversionService;
    private final GUIService guiService;
    private final IssueServiceFactory issueServiceFactory;
    private final ExtensionManager extensionManager;

    @Autowired
    public IssueSearchProvider(RepositoryService repositoryService, SubversionService subversionService, GUIService guiService, IssueServiceFactory issueServiceFactory, ExtensionManager extensionManager) {
        this.repositoryService = repositoryService;
        this.subversionService = subversionService;
        this.guiService = guiService;
        this.issueServiceFactory = issueServiceFactory;
        this.extensionManager = extensionManager;
    }

    @Override
    public boolean isTokenSearchable(String token) {
        for (IssueServiceSummary issueServiceSummary : issueServiceFactory.getAllServices()) {
            IssueService service = issueServiceFactory.getServiceByName(issueServiceSummary.getId());
            if (service.isIssue(token)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Collection<SearchResult> search(String key) {
        if (extensionManager.isExtensionEnabled(SVNExplorerExtension.EXTENSION)) {
            Collection<SearchResult> results = new ArrayList<>();
            for (SVNRepository repository : repositoryService.getAllRepositories()) {
                // Is the issue indexed in this repository?
                if (subversionService.isIndexedIssue(repository, key)) {
                    SearchResult result = new SearchResult(
                            key,
                            new LocalizableMessage("svnexplorer.search.key", key, repository.getName()),
                            guiService.toGUI(String.format("extension/svnexplorer/repository/%d/issue/%s", repository.getId(), key)),
                            80
                    );
                    results.add(result);
                }
            }
            return results;
        } else {
            return Collections.emptySet();
        }
    }
}
