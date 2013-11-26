package net.ontrack.extension.jenkins;

import net.ontrack.core.model.Entity;
import net.ontrack.core.security.AuthorizationPolicy;
import net.ontrack.core.security.ProjectFunction;
import net.ontrack.extension.api.property.AbstractLinkPropertyExtensionDescriptor;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

@Component
public class JenkinsUrlPropertyDescriptor extends AbstractLinkPropertyExtensionDescriptor {

    public static final String NAME = "url";

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
        return NAME;
    }

    /**
     * Editable only by administrators on branches & validation stamps
     */
    @Override
    public AuthorizationPolicy getEditingAuthorizationPolicy(Entity entity) {
        switch (entity) {
            case BRANCH:
                return AuthorizationPolicy.PROJECT_CONFIG;
            case VALIDATION_STAMP:
                return AuthorizationPolicy.forProject(ProjectFunction.PROJECT_CONFIG, ProjectFunction.VALIDATION_STAMP_MODIFY);
            default:
                return super.getEditingAuthorizationPolicy(entity);
        }
    }
}
