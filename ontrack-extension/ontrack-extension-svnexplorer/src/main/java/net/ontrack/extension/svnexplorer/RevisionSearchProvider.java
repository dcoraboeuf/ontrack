package net.ontrack.extension.svnexplorer;

import net.ontrack.core.model.SearchResult;
import net.ontrack.extension.api.ExtensionManager;
import net.ontrack.extension.svn.RevisionNotFoundException;
import net.ontrack.extension.svn.service.SubversionService;
import net.ontrack.service.GUIService;
import net.ontrack.service.SearchProvider;
import net.sf.jstring.LocalizableMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

@Component
public class RevisionSearchProvider implements SearchProvider {

    private final SubversionService subversionService;
    private final GUIService guiService;
    private final ExtensionManager extensionManager;

    @Autowired
    public RevisionSearchProvider(SubversionService subversionService, GUIService guiService, ExtensionManager extensionManager) {
        this.subversionService = subversionService;
        this.guiService = guiService;
        this.extensionManager = extensionManager;
    }

    @Override
    public boolean isTokenSearchable(String token) {
        return Pattern.matches("\\d+", token);
    }

    @Override
    public Collection<SearchResult> search(String token) {
        if (extensionManager.isExtensionEnabled(SVNExplorerExtension.EXTENSION)) {
            long revision = Long.parseLong(token, 10);
            try {
                subversionService.getRevisionInfo(revision);
                return Collections.singleton(
                        new SearchResult(
                                String.valueOf(revision),
                                new LocalizableMessage("svnexplorer.search.revision", revision),
                                guiService.toGUI(String.format("extension/svnexplorer/revision/%d", revision))
                        )
                );
            } catch (RevisionNotFoundException ex) {
                return Collections.emptyList();
            }
        } else {
            return null;
        }
    }

}
