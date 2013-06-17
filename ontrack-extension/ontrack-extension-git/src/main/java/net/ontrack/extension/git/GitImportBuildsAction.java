package net.ontrack.extension.git;

import net.ontrack.core.model.BranchSummary;
import net.ontrack.core.model.Entity;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.core.ui.ManageUI;
import net.ontrack.extension.api.action.EntityActionExtension;
import net.ontrack.extension.api.property.PropertiesService;
import net.ontrack.extension.git.model.GitImportBuildsForm;
import net.ontrack.extension.git.service.GitService;
import net.ontrack.web.support.AbstractGUIController;
import net.ontrack.web.support.EntityConverter;
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
@RequestMapping("/gui/extension/git/import-builds")
public class GitImportBuildsAction extends AbstractGUIController implements EntityActionExtension<BranchSummary> {

    private final SecurityUtils securityUtils;
    private final PropertiesService propertiesService;
    private final ManageUI manageUI;
    private final EntityConverter entityConverter;
    private final GitService gitService;

    @Autowired
    public GitImportBuildsAction(ErrorHandler errorHandler, SecurityUtils securityUtils, PropertiesService propertiesService, ManageUI manageUI, EntityConverter entityConverter, GitService gitService) {
        super(errorHandler);
        this.securityUtils = securityUtils;
        this.propertiesService = propertiesService;
        this.manageUI = manageUI;
        this.entityConverter = entityConverter;
        this.gitService = gitService;
    }

    @RequestMapping(value = "/{projectName:[A-Za-z0-9_\\.\\-]+}/{branchName:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.GET)
    public String importBuildsConfig(Locale locale, @PathVariable String projectName, @PathVariable String branchName, Model model) {
        securityUtils.checkIsAdmin();
        model.addAttribute("branch", manageUI.getBranch(projectName, branchName));
        // OK
        return "extension/git/import-builds";
    }

    @RequestMapping(value = "/{projectName:[A-Za-z0-9_\\.\\-]+}/{branchName:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.POST)
    public String importBuilds(@PathVariable String projectName, @PathVariable String branchName, GitImportBuildsForm form) {
        securityUtils.checkIsAdmin();
        // Import of the builds
        gitService.importBuilds(entityConverter.getBranchId(projectName, branchName), form);
        // OK
        return String.format("redirect:/gui/project/%s/branch/%s", projectName, branchName);
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
