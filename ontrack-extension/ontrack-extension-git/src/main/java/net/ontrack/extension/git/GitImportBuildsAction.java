package net.ontrack.extension.git;

import net.ontrack.core.model.BranchSummary;
import net.ontrack.core.model.Entity;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.extension.api.action.EntityActionExtension;
import net.ontrack.extension.api.property.PropertiesService;
import net.ontrack.web.support.AbstractGUIController;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Localizable;
import net.sf.jstring.LocalizableMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/gui/extension/git/import-builds")
public class GitImportBuildsAction extends AbstractGUIController implements EntityActionExtension<BranchSummary> {

    private final PropertiesService propertiesService;

    @Autowired
    public GitImportBuildsAction(ErrorHandler errorHandler, PropertiesService propertiesService) {
        super(errorHandler);
        this.propertiesService = propertiesService;
    }

    @Override
    public Entity getScope() {
        return Entity.BRANCH;
    }

    @Override
    public String getExtension() {
        return GitExtension.EXTENSION;
    }

    @Override
    public String getName() {
        return "import-builds";
    }

    @Override
    public String getRole(BranchSummary summary) {
        return SecurityRoles.ADMINISTRATOR;
    }

    @Override
    public boolean isEnabled(BranchSummary summary) {
        String remote = propertiesService.getPropertyValue(Entity.PROJECT, summary.getProject().getId(), GitExtension.EXTENSION, GitRemoteProperty.NAME);
        String branch = propertiesService.getPropertyValue(Entity.BRANCH, summary.getId(), GitExtension.EXTENSION, GitBranchProperty.NAME);
        return StringUtils.isNotBlank(remote) && StringUtils.isNotBlank(branch);
    }

    @Override
    public String getPath(BranchSummary summary) {
        return String.format("gui/extension/git/import-builds/%s/%s", summary.getProject().getName(), summary.getName());
    }

    @Override
    public Localizable getTitle(BranchSummary summary) {
        return new LocalizableMessage("git.import-builds", summary.getProject().getName(), summary.getName());
    }

    @Override
    public String getIcon(BranchSummary summary) {
        return "icon-download";
    }

    @Override
    public String getCss(BranchSummary summary) {
        return null;
    }
}
