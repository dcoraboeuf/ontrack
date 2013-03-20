package net.ontrack.extension.api;

import net.ontrack.core.model.Entity;
import net.ontrack.extension.api.configuration.ConfigurationExtension;
import net.ontrack.extension.api.property.PropertyExtensionDescriptor;
import net.ontrack.extension.api.property.PropertyExtensionNotFoundException;

import java.util.Collection;
import java.util.List;

public interface ExtensionManager {

    /**
     * Gets the list of extensions
     */
    Collection<Extension> getExtensions();

    /**
     * Gets an extension by its name
     * @throws ExtensionNotFoundException If no extension with this name is defined
     */
    <T extends Extension> T getExtension (String name) throws ExtensionNotFoundException;

    /**
     * Gets a property extension descriptor associated with this extension and this name.
     *
     * @param extension Extension ID
     * @param name      Property name
     * @return Descriptor if found
     * @throws net.ontrack.extension.api.property.PropertyExtensionNotFoundException
     *          If not found
     */
    PropertyExtensionDescriptor getPropertyExtensionDescriptor(String extension, String name) throws PropertyExtensionNotFoundException;

    /**
     * Returns the list of properties applicable for this entity
     */
    List<PropertyExtensionDescriptor> getPropertyExtensionDescriptors(Entity entity);

    /**
     * Gets the list of all configuration extensions
     */
    Collection<ConfigurationExtension> getConfigurationExtensions();
}
