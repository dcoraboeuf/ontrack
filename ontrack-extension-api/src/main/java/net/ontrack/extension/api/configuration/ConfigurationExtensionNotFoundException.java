package net.ontrack.extension.api.configuration;

import net.ontrack.core.support.InputException;

/**
 * Thrown when a configuration extension is not found.
 */
public class ConfigurationExtensionNotFoundException extends InputException {
    public ConfigurationExtensionNotFoundException(String extension, String name) {
        super(extension, name);
    }
}
