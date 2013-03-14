package net.ontrack.backend;

import net.ontrack.core.model.Entity;
import net.ontrack.core.support.InputException;

public class PropertyScopeException extends InputException {
    public PropertyScopeException(String extension, String name, Entity entity) {
        super(extension, name, entity.name());
    }
}
