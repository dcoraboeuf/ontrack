package net.ontrack.extension.jira;

import net.ontrack.extension.api.configuration.ConfigurationExtension;
import net.ontrack.extension.api.property.PropertyExtensionDescriptor;
import net.ontrack.extension.api.support.ExtensionAdapter;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class JIRAExtension extends ExtensionAdapter {

    public static final String EXTENSION = "jira";

    public JIRAExtension() {
        super(EXTENSION);
    }

    @Override
    public List<? extends PropertyExtensionDescriptor> getPropertyExtensionDescriptors() {
        return Collections.singletonList(new JIRAIssuePropertyExtension());
    }

    @Override
    public List<? extends ConfigurationExtension> getConfigurationExtensions() {
        return Collections.singletonList(new JIRAConfigurationExtension());
    }
}
