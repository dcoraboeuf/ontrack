package net.ontrack.extension.api.support;

import net.ontrack.extension.api.Extension;
import net.ontrack.extension.api.configuration.ConfigurationExtension;
import net.ontrack.extension.api.property.PropertyExtensionDescriptor;

import java.util.List;

public abstract class ExtensionAdapter implements Extension {

    private final String name;
    private final List<? extends PropertyExtensionDescriptor> propertyExtensionDescriptors;
    private final List<? extends ConfigurationExtension> configurationExtensions;

    protected ExtensionAdapter(String name, List<? extends PropertyExtensionDescriptor> propertyExtensionDescriptors, List<? extends ConfigurationExtension> configurationExtensions) {
        this.name = name;
        this.propertyExtensionDescriptors = propertyExtensionDescriptors;
        this.configurationExtensions = configurationExtensions;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<? extends PropertyExtensionDescriptor> getPropertyExtensionDescriptors() {
        return propertyExtensionDescriptors;
    }

    @Override
    public List<? extends ConfigurationExtension> getConfigurationExtensions() {
        return configurationExtensions;
    }
}
