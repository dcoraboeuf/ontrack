package net.ontrack.extension.svnexplorer;

import net.ontrack.core.model.Entity;
import net.ontrack.core.security.AuthorizationPolicy;
import net.ontrack.extension.api.property.AbstractPropertyExtensionDescriptor;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

@Component
public class ProjectRootPathPropertyExtension extends AbstractPropertyExtensionDescriptor {

    public static final String NAME = "rootPath";

    @Override
    public EnumSet<Entity> getScope() {
        return EnumSet.of(Entity.PROJECT);
    }

    @Override
    public String getExtension() {
        return SVNExplorerExtension.EXTENSION;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDisplayNameKey() {
        return "svnexplorer.rootPath";
    }

    @Override
    public String getIconPath() {
        return "extension/subversion.png";
    }

    @Override
    public AuthorizationPolicy getEditingAuthorizationPolicy(Entity entity) {
        return AuthorizationPolicy.PROJECT_CONFIG;
    }

    @Override
    public AuthorizationPolicy getViewingAuthorizationPolicy(Entity entity) {
        return AuthorizationPolicy.PROJECT_CONFIG;
    }
}
