package net.ontrack.extension.git;

import net.ontrack.core.model.Entity;
import net.ontrack.core.security.AuthorizationPolicy;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

@Component
public class GitCommitLinkProperty extends AbstractGitProperty {

    public static final String NAME = "commitLink";

    @Override
    public EnumSet<Entity> getScope() {
        return EnumSet.of(Entity.PROJECT);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDisplayNameKey() {
        return "git.commitLink";
    }

    @Override
    public AuthorizationPolicy getViewingAuthorizationPolicy(Entity entity) {
        return AuthorizationPolicy.PROJECT_CONFIG;
    }
}
