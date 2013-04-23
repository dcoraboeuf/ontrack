package net.ontrack.extension.pkg;

import net.ontrack.core.model.Entity;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.extension.api.property.AbstractPropertyExtensionDescriptor;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

@Component
public class PackagePropertyDescriptor extends AbstractPropertyExtensionDescriptor {

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
        return "package";
    }

    @Override
    public String getDisplayNameKey() {
        return "package";
    }

    /**
     * Editable by controllers on all entities
     */
    @Override
    public String getRoleForEdition(Entity entity) {
        return SecurityRoles.CONTROLLER;
    }
}
