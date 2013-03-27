package net.ontrack.extension.svnexplorer.ui;

import net.ontrack.core.model.BuildSummary;
import net.ontrack.core.ui.ManageUI;
import net.ontrack.extension.svnexplorer.model.ChangeLogRequest;
import net.ontrack.extension.svnexplorer.model.ChangeLogSummary;
import net.ontrack.extension.svnexplorer.service.SVNExplorerService;
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
    private final SVNExplorerService svnExplorerService;

    @Autowired
    public SVNExplorerUIController(ErrorHandler errorHandler, Strings strings, ManageUI manageUI, SVNExplorerService svnExplorerService) {
        super(errorHandler, strings);
        this.manageUI = manageUI;
        this.svnExplorerService = svnExplorerService;
    }

    @Override
    @RequestMapping(value = "/changelog", method = RequestMethod.POST)
    public
    @ResponseBody
    ChangeLogSummary getChangeLogSummary(@RequestBody ChangeLogRequest request) {
        // Build information
        BuildSummary buildFrom = manageUI.getBuild(request.getProject(), request.getBranch(), request.getFrom());
        BuildSummary buildTo = manageUI.getBuild(request.getProject(), request.getBranch(), request.getTo());
        // OK
        return svnExplorerService.getChangeLogSummary(buildFrom.getBranch().getId(), buildFrom.getId(), buildTo.getId());
    }
}
