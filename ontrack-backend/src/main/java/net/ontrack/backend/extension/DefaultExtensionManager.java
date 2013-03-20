package net.ontrack.backend.extension;

import net.ontrack.core.model.Entity;
import net.ontrack.extension.api.Extension;
import net.ontrack.extension.api.ExtensionManager;
import net.ontrack.extension.api.ExtensionNotFoundException;
import net.ontrack.extension.api.configuration.ConfigurationExtension;
import net.ontrack.extension.api.property.PropertyExtensionDescriptor;
import net.ontrack.extension.api.property.PropertyExtensionNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Implementation of the extension manager.
 */
@Component
public class DefaultExtensionManager implements ExtensionManager {

    private final Map<String, Extension> extensionIndex;
    private final Map<String, Map<String, PropertyExtensionDescriptor>> propertyIndex;
    private final Collection<ConfigurationExtension> configurationExtensions;

    @Autowired
    public DefaultExtensionManager(Collection<Extension> extensions) {
        Logger logger = LoggerFactory.getLogger(ExtensionManager.class);

        /**
         * Indexation of extensions
         */
        logger.info("[extension] Indexing extensions");
        extensionIndex = new TreeMap<>();
        propertyIndex = new HashMap<>();
        configurationExtensions = new ArrayList<>();
        for (Extension extension : extensions) {
            String extensionName = extension.getName();
            extensionIndex.put(extensionName, extension);

            /**
             * Indexation of properties
             */

            logger.info("[extension] Indexing property extensions");
            for (PropertyExtensionDescriptor descriptor : extension.getPropertyExtensionDescriptors()) {
                String name = descriptor.getName();
                // Logging
                logger.info("[extension] Property extension={}, name={}", extensionName, name);
                // Index per extension
                Map<String, PropertyExtensionDescriptor> extensionPropertyIndex = propertyIndex.get(extensionName);
                if (extensionPropertyIndex == null) {
                    extensionPropertyIndex = new HashMap<>();
                    propertyIndex.put(extensionName, extensionPropertyIndex);
                }
                // Index per name
                if (extensionPropertyIndex.containsKey(name)) {
                    logger.warn("[extension] Property name {} already defined for extension {}", name, extensionName);
                }
                extensionPropertyIndex.put(name, descriptor);
            }

            /**
             * Indexation of configurations
             */
            logger.info("[extension] Indexing configuration extensions");
            for (ConfigurationExtension configurationExtension : extension.getConfigurationExtensions()) {
                // Logging
                logger.info("[extension] Configuration extension={}, configuration={}", extensionName, configurationExtension);
                // Adds to the list
                configurationExtensions.add(configurationExtension);
            }
        }
    }

    @Override
    public List<PropertyExtensionDescriptor> getPropertyExtensionDescriptors(Entity entity) {
        List<PropertyExtensionDescriptor> list = new ArrayList<>();
        for (Map<String, PropertyExtensionDescriptor> extensionIndex : propertyIndex.values()) {
            for (PropertyExtensionDescriptor descriptor : extensionIndex.values()) {
                if (descriptor.getScope().contains(entity)) {
                    list.add(descriptor);
                }
            }
        }
        return list;
    }

    @Override
    public Collection<ConfigurationExtension> getConfigurationExtensions() {
        return configurationExtensions;
    }

    @Override
    public Collection<Extension> getExtensions() {
        return extensionIndex.values();
    }

    @Override
    public <T extends Extension> T getExtension(String name) throws ExtensionNotFoundException {
        Extension extension = extensionIndex.get(name);
        if (extension != null) {
            return (T) extension;
        } else {
            throw new ExtensionNotFoundException(name);
        }
    }

    @Override
    public PropertyExtensionDescriptor getPropertyExtensionDescriptor(String extension, String name) throws PropertyExtensionNotFoundException {
        Map<String, PropertyExtensionDescriptor> extensionIndex = propertyIndex.get(extension);
        if (extensionIndex != null) {
            PropertyExtensionDescriptor descriptor = extensionIndex.get(name);
            if (descriptor != null) {
                return descriptor;
            }
        }
        throw new PropertyExtensionNotFoundException(extension, name);
    }

}
