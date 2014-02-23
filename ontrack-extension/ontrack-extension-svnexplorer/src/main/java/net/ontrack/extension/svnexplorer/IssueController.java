package net.ontrack.extension.svnexplorer;

import net.ontrack.extension.svnexplorer.ui.SVNExplorerUI;
import net.ontrack.web.support.AbstractGUIController;
import net.ontrack.web.support.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Locale;

@Controller
public class IssueController extends AbstractGUIController {

    private final SVNExplorerUI ui;

    @Autowired
    public IssueController(ErrorHandler errorHandler, SVNExplorerUI ui) {
        super(errorHandler);
        this.ui = ui;
    }


    @RequestMapping(value = "/gui/extension/svnexplorer/repository/{repositoryId:\\d+}/issue/{key:.*}", method = RequestMethod.GET)
    public String issue(Locale locale, @PathVariable int repositoryId, @PathVariable String key, Model model) {
        // Issue info
        model.addAttribute("info", ui.getIssueInfo(locale, repositoryId, key));
        // OK
        return "extension/svnexplorer/repository-issue";
    }

}
