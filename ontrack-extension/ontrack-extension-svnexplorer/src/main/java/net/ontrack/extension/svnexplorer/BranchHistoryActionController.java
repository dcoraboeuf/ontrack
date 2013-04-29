package net.ontrack.extension.svnexplorer;

import net.ontrack.core.model.Entity;
import net.ontrack.core.model.ProjectSummary;
import net.ontrack.extension.api.action.EntityActionExtension;
import net.ontrack.web.support.AbstractGUIController;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Localizable;
import net.sf.jstring.LocalizableMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/gui/extension/svnexplorer/branch-history")
public class BranchHistoryActionController extends AbstractGUIController implements EntityActionExtension<ProjectSummary> {

    @Autowired
    public BranchHistoryActionController(ErrorHandler errorHandler) {
        super(errorHandler);
    }

    @Override
    public Entity getScope() {
        return Entity.PROJECT;
    }

    @Override
    public String getExtension() {
        return SVNExplorerExtension.EXTENSION;
    }

    @Override
    public String getName() {
        return "branch-history";
    }

    @Override
    public String getRole(ProjectSummary summary) {
        return null;
    }

    @Override
    public String getPath(ProjectSummary summary) {
        return String.format("gui/extension/svnexplorer/branch-history/%s", summary.getName());
    }

    @Override
    public Localizable getTitle(ProjectSummary summary) {
        return new LocalizableMessage("svnexplorer.branch-history");
    }
}
