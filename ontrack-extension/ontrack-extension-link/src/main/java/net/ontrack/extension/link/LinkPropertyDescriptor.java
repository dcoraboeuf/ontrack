package net.ontrack.extension.link;

import net.ontrack.core.model.Entity;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.extension.api.support.AbstractLinkPropertyExtensionDescriptor;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

@Component
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
        return "link";
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
