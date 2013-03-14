package net.ontrack.extension.api;

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
}
