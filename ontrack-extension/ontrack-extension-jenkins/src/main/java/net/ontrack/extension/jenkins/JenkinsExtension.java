package net.ontrack.extension.jenkins;

import net.ontrack.extension.api.property.PropertyExtensionDescriptor;
import net.ontrack.extension.api.support.ExtensionAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class JenkinsExtension extends ExtensionAdapter {

    public static final String EXTENSION = "jenkins";
    private final JenkinsUrlPropertyDescriptor jenkinsUrlPropertyDescriptor;

    @Autowired
    public JenkinsExtension(JenkinsUrlPropertyDescriptor jenkinsUrlPropertyDescriptor) {
        super(EXTENSION);
        this.jenkinsUrlPropertyDescriptor = jenkinsUrlPropertyDescriptor;
    }

    @Override
    public List<? extends PropertyExtensionDescriptor> getPropertyExtensionDescriptors() {
        return Collections.singletonList(jenkinsUrlPropertyDescriptor);
    }
}
