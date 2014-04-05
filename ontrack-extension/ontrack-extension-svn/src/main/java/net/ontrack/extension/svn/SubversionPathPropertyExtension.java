package net.ontrack.extension.svn;

import net.ontrack.core.model.BranchSummary;
import net.ontrack.core.model.Entity;
import net.ontrack.core.security.AuthorizationPolicy;
import net.ontrack.extension.api.property.AbstractPropertyExtensionDescriptor;
import net.ontrack.extension.svn.service.SubversionService;
import net.ontrack.extension.svn.service.model.SVNRepository;
import net.ontrack.service.ManagementService;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Locale;

@Component
public class SubversionPathPropertyExtension extends AbstractPropertyExtensionDescriptor {

    public static final String PATH = "path";
    private final SubversionService subversionService;
    private final ManagementService managementService;

    @Autowired
    public SubversionPathPropertyExtension(SubversionService subversionService, ManagementService managementService) {
        this.subversionService = subversionService;
        this.managementService = managementService;
    }

    @Override
    public EnumSet<Entity> getScope() {
        return EnumSet.of(Entity.BRANCH);
    }

    @Override
    public String getExtension() {
        return SubversionExtension.EXTENSION;
    }

    @Override
    public String getName() {
        return PATH;
    }

    @Override
    public String getDisplayNameKey() {
        return "subversion.path";
    }

    @Override
    public String getIconPath() {
        return "extension/subversion.png";
    }

    @Override
    public String toHTML(Strings strings, Locale locale, Entity entity, int branchId, String path) {
        // Gets the branch
        BranchSummary branch = managementService.getBranch(branchId);
        // Gets the repository from the project
        SVNRepository repository = subversionService.getRepositoryForProject(branch.getProject().getId());
        if (repository == null) {
            return "";
        } else {
            return String.format(
                    "<a href=\"%s\">%s</a>",
                    subversionService.getBrowsingURL(repository, path),
                    path
            );
        }
    }

    @Override
    public AuthorizationPolicy getEditingAuthorizationPolicy(Entity entity) {
        return AuthorizationPolicy.PROJECT_CONFIG;
    }
}
