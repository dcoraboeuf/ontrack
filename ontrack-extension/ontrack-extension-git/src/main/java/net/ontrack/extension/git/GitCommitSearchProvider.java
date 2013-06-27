package net.ontrack.extension.git;

import net.ontrack.core.model.SearchResult;
import net.ontrack.extension.api.ExtensionManager;
import net.ontrack.extension.git.service.GitService;
import net.ontrack.extension.git.ui.GitUI;
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
public class GitCommitSearchProvider extends AbstractGUIController implements SearchProvider {

    private final ExtensionManager extensionManager;
    private final GitService gitService;
    private final GUIService guiService;
    private final GitUI gitUI;

    @Autowired
    public GitCommitSearchProvider(ErrorHandler errorHandler, ExtensionManager extensionManager, GitService gitService, GUIService guiService, GitUI gitUI) {
        super(errorHandler);
        this.extensionManager = extensionManager;
        this.gitService = gitService;
        this.guiService = guiService;
        this.gitUI = gitUI;
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

    @RequestMapping(value = "/gui/extension/git/commit/{commit}", method = RequestMethod.GET)
    public String commit(Locale locale, @PathVariable String commit, Model model) {
        // Commit info
        model.addAttribute("commit", gitUI.getCommitInfo(locale, commit));
        // OK
        return "extension/git/commit";
    }

}
