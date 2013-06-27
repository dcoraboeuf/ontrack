package net.ontrack.extension.git;

import net.ontrack.core.model.SearchResult;
import net.ontrack.extension.api.ExtensionManager;
import net.ontrack.extension.git.service.GitService;
import net.ontrack.service.GUIService;
import net.ontrack.service.SearchProvider;
import net.sf.jstring.LocalizableMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

@Component
public class GitCommitSearchProvider implements SearchProvider {

    private final ExtensionManager extensionManager;
    private final GitService gitService;
    private final GUIService guiService;

    @Autowired
    public GitCommitSearchProvider(ExtensionManager extensionManager, GitService gitService, GUIService guiService) {
        this.extensionManager = extensionManager;
        this.gitService = gitService;
        this.guiService = guiService;
    }

    @Override
    public boolean isTokenSearchable(String token) {
        return Pattern.matches("[a-f0-9]{40}", token);
    }

    @Override
    public Collection<SearchResult> search(String commit) {
        if (extensionManager.isExtensionEnabled(GitExtension.EXTENSION)) {
            if (gitService.isCommitDefined(commit)) {
                return Collections.singleton(
                        new SearchResult(
                                commit,
                                new LocalizableMessage("git.search.commit", commit),
                                guiService.toGUI(String.format("extension/git/commit/%s", commit))
                        )
                );
            } else {
                return Collections.emptyList();
            }
        } else {
            return Collections.emptyList();
        }
    }

}
