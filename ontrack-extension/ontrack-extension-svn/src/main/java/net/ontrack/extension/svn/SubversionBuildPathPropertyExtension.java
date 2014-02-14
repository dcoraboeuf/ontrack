package net.ontrack.extension.svn;

import net.ontrack.core.model.Entity;
import net.ontrack.core.security.AuthorizationPolicy;
import net.ontrack.extension.api.property.AbstractPropertyExtensionDescriptor;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

@Component
@Deprecated
public class SubversionBuildPathPropertyExtension extends AbstractPropertyExtensionDescriptor {

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
        return SubversionExtension.SUBVERSION_BUILD_PATH;
    }

    @Override
    public String getDisplayNameKey() {
        return "subversion.buildPath";
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
