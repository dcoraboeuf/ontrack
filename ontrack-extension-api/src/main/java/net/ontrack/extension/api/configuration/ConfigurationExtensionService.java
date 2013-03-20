package net.ontrack.extension.api.configuration;

import java.util.Collection;
import java.util.Map;

public interface ConfigurationExtensionService {

    Collection<? extends ConfigurationExtension> getConfigurationExtensions();

    String saveExtensionConfiguration(String extension, String name, Map<String, String> parameters);

}
