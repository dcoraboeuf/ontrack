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
public class RevisionController extends AbstractGUIController {

    private final SVNExplorerUI ui;

    @Autowired
    public RevisionController(ErrorHandler errorHandler, SVNExplorerUI ui) {
        super(errorHandler);
        this.ui = ui;
    }

    @RequestMapping(value = "/gui/extension/svnexplorer/repository/{repository:\\d+}/revision/{revision:\\d+}", method = RequestMethod.GET)
    public String revision(Locale locale, @PathVariable int repository, @PathVariable long revision, Model model) {
        // Revision info
        model.addAttribute("info", ui.getRevisionInfo(locale, repository, revision));
        // OK
        // FIXME Rename the page
        return "extension/svnexplorer/repository-revision";
    }

}
