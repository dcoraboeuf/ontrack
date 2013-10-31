package net.ontrack.extension.svnexplorer;

import net.ontrack.core.model.Entity;
import net.ontrack.core.model.ProjectSummary;
import net.ontrack.core.security.AuthorizationPolicy;
import net.ontrack.extension.api.action.EntityActionExtension;
import net.ontrack.extension.api.property.PropertiesService;
import net.ontrack.extension.svnexplorer.ui.SVNExplorerUI;
import net.ontrack.web.support.AbstractGUIController;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Localizable;
import net.sf.jstring.LocalizableMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Locale;

@Controller
@RequestMapping("/gui/extension/svnexplorer/branch-history")
public class BranchHistoryActionController extends AbstractGUIController implements EntityActionExtension<ProjectSummary> {

    private final SVNExplorerUI svnExplorerUI;
    private final PropertiesService propertiesService;

    @Autowired
    public BranchHistoryActionController(ErrorHandler errorHandler, SVNExplorerUI svnExplorerUI, PropertiesService propertiesService) {
        super(errorHandler);
        this.svnExplorerUI = svnExplorerUI;
        this.propertiesService = propertiesService;
    }

    @RequestMapping(value = "/{projectName:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.GET)
    public String branchHistory(Locale locale, @PathVariable String projectName, Model model) {
        model.addAttribute("branchHistory", svnExplorerUI.getBranchHistory(locale, projectName));
        // OK
        return "extension/svnexplorer/branch-history";
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
    public AuthorizationPolicy getAuthorizationPolicy(ProjectSummary summary) {
        return AuthorizationPolicy.ALLOW_ALL;
    }

    @Override
    public boolean isEnabled(ProjectSummary summary) {
        String rootPath = propertiesService.getPropertyValue(Entity.PROJECT, summary.getId(), SVNExplorerExtension.EXTENSION, ProjectRootPathPropertyExtension.NAME);
        return StringUtils.isNotBlank(rootPath);
    }

    @Override
    public String getPath(ProjectSummary summary) {
        return String.format("gui/extension/svnexplorer/branch-history/%s", summary.getName());
    }

    @Override
    public Localizable getTitle(ProjectSummary summary) {
        return new LocalizableMessage("svnexplorer.branch-history");
    }

    @Override
    public String getIcon(ProjectSummary summary) {
        return "icon-align-left";
    }

    @Override
    public String getCss(ProjectSummary summary) {
        return null;
    }
}
