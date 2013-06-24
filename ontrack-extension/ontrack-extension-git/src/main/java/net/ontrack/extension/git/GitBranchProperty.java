package net.ontrack.extension.git;

import net.ontrack.core.model.Entity;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

@Component
public class GitBranchProperty extends AbstractGitProperty {

    public static final String NAME = "branch";

    @Override
    public EnumSet<Entity> getScope() {
        return EnumSet.of(Entity.BRANCH);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDisplayNameKey() {
        return "git.branch";
    }
}
