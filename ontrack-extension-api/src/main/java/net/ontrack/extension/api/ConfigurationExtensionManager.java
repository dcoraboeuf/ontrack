package net.ontrack.extension.api;

import java.util.Collection;

public interface ConfigurationExtensionManager {

    /**
     * Gets the list of all configuration extensions
     */
    Collection<ConfigurationExtension> getConfigurationExtensions();

}
