package net.ontrack.extension.jenkins;

import net.ontrack.core.model.Entity;
import net.ontrack.extension.api.support.AbstractPropertyExtensionDescriptor;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

@Component
public class JenkinsUrlPropertyDescriptor extends AbstractPropertyExtensionDescriptor {

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
}
