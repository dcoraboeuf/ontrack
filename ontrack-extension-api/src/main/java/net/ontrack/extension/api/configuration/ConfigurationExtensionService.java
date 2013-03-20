package net.ontrack.extension.api.configuration;

import java.util.Collection;

public interface ConfigurationExtensionService {

    Collection<? extends ConfigurationExtension> getConfigurationExtensions();

}
