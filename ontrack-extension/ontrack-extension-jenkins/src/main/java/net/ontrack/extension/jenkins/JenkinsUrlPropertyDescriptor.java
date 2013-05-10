package net.ontrack.extension.jenkins;

import net.ontrack.core.model.Entity;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.extension.api.property.AbstractLinkPropertyExtensionDescriptor;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

@Component
public class JenkinsUrlPropertyDescriptor extends AbstractLinkPropertyExtensionDescriptor {

    public JenkinsUrlPropertyDescriptor() {
        super("jenkins.url", "jenkins.png");
    }

    @Override
    public EnumSet<Entity> getScope() {
        return EnumSet.allOf(Entity.class);
    }

    @Override
    public String getExtension() {
        return JenkinsExtension.EXTENSION;
    }

    @Override
    public String getName() {
        return "url";
    }

    /**
     * Editable only by administrators on branches & validation stamps
     */
    @Override
    public String getRoleForEdition(Entity entity) {
        switch (entity) {
            case BRANCH:
            case VALIDATION_STAMP:
                return SecurityRoles.ADMINISTRATOR;
            default:
                return super.getRoleForEdition(entity);
        }
    }
}
