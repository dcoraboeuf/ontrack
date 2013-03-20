package net.ontrack.extension.api;

import net.ontrack.extension.api.configuration.ConfigurationExtension;
import net.ontrack.extension.api.property.PropertyExtensionDescriptor;

import java.util.List;

public interface Extension {

    String getName();

    List<PropertyExtensionDescriptor> getPropertyExtensionDescriptors();

    List<ConfigurationExtension> getConfigurationExtensions();
}
