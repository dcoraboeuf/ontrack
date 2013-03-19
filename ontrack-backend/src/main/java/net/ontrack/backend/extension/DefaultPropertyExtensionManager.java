package net.ontrack.backend.extension;

import net.ontrack.core.model.Entity;
import net.ontrack.extension.api.PropertyExtensionDescriptor;
import net.ontrack.extension.api.PropertyExtensionManager;
import net.ontrack.extension.api.PropertyExtensionNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Implementation of the property extension manager.
 * <p/>
 * At construction time, it scans for all available {@link PropertyExtensionDescriptor} instances
 * and indexes them by extension and name.
 */
@Component
public class DefaultPropertyExtensionManager implements PropertyExtensionManager {

    private final Map<String, Map<String, PropertyExtensionDescriptor>> index;

    @Autowired
    public DefaultPropertyExtensionManager(Collection<PropertyExtensionDescriptor> descriptors) {
        Logger logger = LoggerFactory.getLogger(PropertyExtensionManager.class);
        logger.info("[extension] Initializing DefaultPropertyExtensionManager");
        index = new HashMap<>();
        // Indexing all descriptors
        for (PropertyExtensionDescriptor descriptor : descriptors) {
            String extension = descriptor.getExtension();
            String name = descriptor.getName();
            // Logging
            logger.info("[extension] PropertyExtensionDescriptor extension={}, name={}", extension, name);
            // Index per extension
            Map<String, PropertyExtensionDescriptor> extensionIndex = index.get(extension);
            if (extensionIndex == null) {
                extensionIndex = new HashMap<>();
                index.put(extension, extensionIndex);
            }
            // Index per name
            if (extensionIndex.containsKey(name)) {
                logger.warn("[extension] PropertyExtensionDescriptor Name {} already defined for extension {}", name, extension);
            }
            extensionIndex.put(name, descriptor);
        }
    }

    @Override
    public List<PropertyExtensionDescriptor> getPropertyExtensionDescriptors(Entity entity) {
        List<PropertyExtensionDescriptor> list = new ArrayList<>();
        for (Map<String, PropertyExtensionDescriptor> extensionIndex : index.values()) {
            for (PropertyExtensionDescriptor descriptor : extensionIndex.values()) {
                if (descriptor.getScope().contains(entity)) {
                    list.add(descriptor);
                }
            }
        }
        return list;
    }

    @Override
    public PropertyExtensionDescriptor getPropertyExtensionDescriptor(String extension, String name) throws PropertyExtensionNotFoundException {
        Map<String, PropertyExtensionDescriptor> extensionIndex = index.get(extension);
        if (extensionIndex != null) {
            PropertyExtensionDescriptor descriptor = extensionIndex.get(name);
            if (descriptor != null) {
                return descriptor;
            }
        }
        throw new PropertyExtensionNotFoundException(extension, name);
    }

}
