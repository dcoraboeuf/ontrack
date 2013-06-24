package net.ontrack.extension.git;

import net.ontrack.core.model.Entity;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

@Component
public class GitRemoteProperty extends AbstractGitProperty {

    public static final String NAME = "remote";

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
        return "git.remote";
    }


}
