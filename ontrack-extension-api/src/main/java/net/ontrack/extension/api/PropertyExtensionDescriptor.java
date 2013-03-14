package net.ontrack.extension.api;

import net.ontrack.core.model.Entity;
import net.ontrack.core.support.InputException;

import java.util.EnumSet;

public interface PropertyExtensionDescriptor {

    /**
     * Scope of the property
     *
     * @return List of {@link Entity} this property can apply to
     */
    EnumSet<Entity> getScope();

    /**
     * Validates a value
     *
     * @param value Value to validate
     * @throws InputException If not valid
     */
    void validate(String value) throws InputException;

    /**
     * Name of the associated extension
     *
     * @return Extension ID
     */
    String getExtension();

    /**
     * Name of the property
     *
     * @return Property name
     */
    String getName();
}
