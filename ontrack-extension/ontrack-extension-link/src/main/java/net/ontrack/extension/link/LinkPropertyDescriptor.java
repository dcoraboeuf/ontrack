package net.ontrack.extension.link;

import net.ontrack.core.model.Entity;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.extension.api.property.AbstractLinkPropertyExtensionDescriptor;

import java.util.EnumSet;

public class LinkPropertyDescriptor extends AbstractLinkPropertyExtensionDescriptor {

    public LinkPropertyDescriptor() {
        super("link", "link.png");
    }

    @Override
    public EnumSet<Entity> getScope() {
        return EnumSet.allOf(Entity.class);
    }

    @Override
    public String getExtension() {
        return LinkExtension.EXTENSION;
    }

    @Override
    public String getName() {
        return "url";
    }

    /**
     * Editable by administrators on all entities
     */
    @Override
    public String getRoleForEdition(Entity entity) {
        return SecurityRoles.ADMINISTRATOR;
    }
}
