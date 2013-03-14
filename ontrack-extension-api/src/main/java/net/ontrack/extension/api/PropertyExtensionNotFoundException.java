package net.ontrack.extension.api;

import net.ontrack.core.support.InputException;

/**
 * Thrown when a property extension is not found.
 */
public class PropertyExtensionNotFoundException extends InputException {
    public PropertyExtensionNotFoundException(String extension, String name) {
        super(extension, name);
    }
}
