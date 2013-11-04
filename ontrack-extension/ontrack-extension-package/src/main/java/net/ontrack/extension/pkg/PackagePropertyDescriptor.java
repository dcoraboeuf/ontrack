package net.ontrack.extension.pkg;

import net.ontrack.core.model.Entity;
import net.ontrack.core.security.AuthorizationPolicy;
import net.ontrack.core.security.ProjectFunction;
import net.ontrack.extension.api.property.AbstractPropertyExtensionDescriptor;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

@Component
public class PackagePropertyDescriptor extends AbstractPropertyExtensionDescriptor {

    public static final String PACKAGE = "package";

    @Override
    public EnumSet<Entity> getScope() {
        return EnumSet.of(Entity.BUILD);
    }

    @Override
    public String getExtension() {
        return PackageExtension.EXTENSION;
    }

    @Override
    public String getName() {
        return PACKAGE;
    }

    @Override
    public String getDisplayNameKey() {
        return "package";
    }

    @Override
    public String getIconPath() {
        return "extension/package.png";
    }

    @Override
    public AuthorizationPolicy getEditingAuthorizationPolicy(Entity entity) {
        return AuthorizationPolicy.forProject(ProjectFunction.BUILD_CREATE, ProjectFunction.BUILD_MODIFY);
    }
}
