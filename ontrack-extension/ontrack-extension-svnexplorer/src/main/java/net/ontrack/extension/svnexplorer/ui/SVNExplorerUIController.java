package net.ontrack.extension.svnexplorer.ui;

import net.ontrack.core.model.BranchSummary;
import net.ontrack.core.ui.ManageUI;
import net.ontrack.extension.svnexplorer.model.ChangeLogRequest;
import net.ontrack.extension.svnexplorer.model.ChangeLogSummary;
import net.ontrack.web.support.AbstractUIController;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/ui/extension/svnexplorer")
public class SVNExplorerUIController extends AbstractUIController implements SVNExplorerUI {

    private final ManageUI manageUI;

    @Autowired
    public SVNExplorerUIController(ErrorHandler errorHandler, Strings strings, ManageUI manageUI) {
        super(errorHandler, strings);
        this.manageUI = manageUI;
    }

    @Override
    @RequestMapping(value = "/changelog", method = RequestMethod.POST)
    public
    @ResponseBody
    ChangeLogSummary getChangeLogSummary(@RequestBody ChangeLogRequest request) {
        // Branch information
        BranchSummary branch = manageUI.getBranch(request.getProject(), request.getBranch());
        // FIXME Implement net.ontrack.extension.svnexplorer.ui.SVNExplorerUIController.getChangeLogSummary
        // OK
        return new ChangeLogSummary(branch);
    }
}
