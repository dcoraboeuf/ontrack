package net.ontrack.extension.git;

import net.ontrack.core.model.Entity;
import net.ontrack.core.security.AuthorizationPolicy;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.extension.api.property.AbstractPropertyExtensionDescriptor;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

public abstract class AbstractGitProperty extends AbstractPropertyExtensionDescriptor {

    @Override
    public String getExtension() {
        return GitExtension.EXTENSION;
    }

    @Override
    public String getIconPath() {
        return "extension/git.png";
    }

    @Override
    public AuthorizationPolicy getEditingAuthorizationPolicy(Entity entity) {
        return AuthorizationPolicy.PROJECT_CONFIG;
    }

}
