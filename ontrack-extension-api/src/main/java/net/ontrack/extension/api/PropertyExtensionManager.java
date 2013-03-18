package net.ontrack.extension.api;

import net.ontrack.core.model.Entity;

import java.util.List;

public interface PropertyExtensionManager {

    /**
     * Gets a property extension descriptor associated with this extension and this name.
     *
     * @param extension Extension ID
     * @param name      Property name
     * @return Descriptor if found
     * @throws PropertyExtensionNotFoundException
     *          If not found
     */
    PropertyExtensionDescriptor getPropertyExtensionDescriptor(String extension, String name) throws PropertyExtensionNotFoundException;

    /**
     * Returns the list of properties applicable for this entity
     */
    List<PropertyExtensionDescriptor> getPropertyExtensionDescriptors(Entity entity);
}
