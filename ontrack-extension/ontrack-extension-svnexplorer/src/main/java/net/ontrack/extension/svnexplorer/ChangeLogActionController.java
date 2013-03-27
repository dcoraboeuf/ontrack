package net.ontrack.extension.svnexplorer;

import net.ontrack.extension.api.action.ActionExtension;
import net.ontrack.extension.svnexplorer.model.ChangeLogRequest;
import net.ontrack.extension.svnexplorer.model.ChangeLogSummary;
import net.ontrack.extension.svnexplorer.ui.SVNExplorerUI;
import net.ontrack.web.support.AbstractGUIController;
import net.ontrack.web.support.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/gui/extension/svnexplorer/changelog")
public class ChangeLogActionController extends AbstractGUIController implements ActionExtension {

    private final SVNExplorerUI ui;

    @Autowired
    public ChangeLogActionController(ErrorHandler errorHandler, SVNExplorerUI ui) {
        super(errorHandler);
        this.ui = ui;
    }


    @RequestMapping(value = "", method = RequestMethod.GET)
    public String changeLogPage (String project, String branch, String from, String to, Model model) {
        // Request
        ChangeLogRequest request = new ChangeLogRequest(project, branch, from, to);
        // Loads the summary
        ChangeLogSummary summary = ui.getChangeLogSummary(request);
        model.addAttribute("summary", summary);
        // OK
        return "extension/svnexplorer/changelog";
    }

    @Override
    public String getExtension() {
        return SVNExplorerExtension.EXTENSION;
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
        return "gui/extension/svnexplorer/changelog";
    }

    @Override
    public String getTitleKey() {
        return "svnexplorer.changelog";
    }
}
