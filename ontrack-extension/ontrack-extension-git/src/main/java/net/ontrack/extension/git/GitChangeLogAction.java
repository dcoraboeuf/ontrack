package net.ontrack.extension.git;

import net.ontrack.extension.api.action.ActionExtension;
import net.ontrack.extension.git.model.ChangeLogRequest;
import net.ontrack.extension.git.model.ChangeLogSummary;
import net.ontrack.extension.git.ui.GitUI;
import net.ontrack.web.support.AbstractGUIController;
import net.ontrack.web.support.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Locale;

@Controller
@RequestMapping("/gui/extension/git/changelog")
public class GitChangeLogAction extends AbstractGUIController implements ActionExtension {

    private final GitUI ui;

    @Autowired
    public GitChangeLogAction(ErrorHandler errorHandler, GitUI ui) {
        super(errorHandler);
        this.ui = ui;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String changeLogPage(Locale locale, String project, String branch, String from, String to, Model model) {
        // Request
        ChangeLogRequest request = new ChangeLogRequest(project, branch, from, to);
        // Loads the summary
        ChangeLogSummary summary = ui.getChangeLogSummary(locale, request);
        model.addAttribute("summary", summary);
        // OK
        return "extension/git/changelog";
    }

    @Override
    public String getExtension() {
        return GitExtension.EXTENSION;
    }

    @Override
    public String getName() {
        return "changelog";
    }

    /**
     * Allowed to everybody.
     */
    @Override
    public String getRole() {
        return null;
    }

    @Override
    public String getPath() {
        return "gui/extension/git/changelog";
    }

    @Override
    public String getTitleKey() {
        return "git.changelog";
    }
}
