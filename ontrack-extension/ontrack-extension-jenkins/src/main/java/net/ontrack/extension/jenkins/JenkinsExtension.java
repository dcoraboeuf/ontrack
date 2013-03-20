package net.ontrack.extension.jenkins;

import net.ontrack.extension.api.property.PropertyExtensionDescriptor;
import net.ontrack.extension.api.support.ExtensionAdapter;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public final class JenkinsExtension extends ExtensionAdapter {

    public static final String EXTENSION = "jenkins";

    public JenkinsExtension() {
        super(EXTENSION);
    }

    @Override
    public List<? extends PropertyExtensionDescriptor> getPropertyExtensionDescriptors() {
        return Collections.singletonList(new JenkinsUrlPropertyDescriptor());
    }
}
