package net.ontrack.extension.svnexplorer;

import net.ontrack.core.model.SearchResult;
import net.ontrack.extension.api.ExtensionManager;
import net.ontrack.extension.jira.service.model.JIRAConfiguration;
import net.ontrack.extension.svn.service.SubversionService;
import net.ontrack.service.GUIService;
import net.ontrack.service.SearchProvider;
import net.sf.jstring.LocalizableMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

@Component
public class IssueSearchProvider implements SearchProvider {

    private final SubversionService subversionService;
    private final GUIService guiService;
    private final ExtensionManager extensionManager;

    @Autowired
    public IssueSearchProvider(SubversionService subversionService, GUIService guiService, ExtensionManager extensionManager) {
        this.subversionService = subversionService;
        this.guiService = guiService;
        this.extensionManager = extensionManager;
    }

    @Override
    public boolean isTokenSearchable(String token) {
        // FIXME Uses all issue services?
        return JIRAConfiguration.ISSUE_PATTERN.matcher(token).matches();
    }

    @Override
    public Collection<SearchResult> search(String key) {
        if (extensionManager.isExtensionEnabled(SVNExplorerExtension.EXTENSION) && subversionService.isIndexedIssue(key)) {
            // FIXME SVN Repository - looping over them
            return Collections.singleton(
                    new SearchResult(
                            key,
                            new LocalizableMessage("svnexplorer.search.key", key),
                            guiService.toGUI(String.format("extension/svnexplorer/issue/%s", key)),
                            80
                    )
            );
        } else {
            return Collections.emptySet();
        }
    }
}
