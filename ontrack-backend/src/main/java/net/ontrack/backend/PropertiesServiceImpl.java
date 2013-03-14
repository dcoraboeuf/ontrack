package net.ontrack.backend;

import net.ontrack.core.model.Entity;
import net.ontrack.core.model.PropertiesCreationForm;
import net.ontrack.core.model.PropertyCreationForm;
import net.ontrack.extension.api.PropertyExtensionDescriptor;
import net.ontrack.extension.api.PropertyExtensionManager;
import net.ontrack.service.PropertiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PropertiesServiceImpl implements PropertiesService {

    private final PropertyExtensionManager propertyExtensionManager;

    @Autowired
    public PropertiesServiceImpl(PropertyExtensionManager propertyExtensionManager) {
        this.propertyExtensionManager = propertyExtensionManager;
    }

    @Override
    @Transactional
    public void createProperties(Entity entity, int entityId, PropertiesCreationForm properties) {
        // For all properties
        for (PropertyCreationForm propertyCreationForm : properties.getList()) {
            String extension = propertyCreationForm.getExtension();
            String name = propertyCreationForm.getName();
            String value = propertyCreationForm.getValue();
            // Gets the property extension descriptor
            PropertyExtensionDescriptor propertyExtensionDescriptor = propertyExtensionManager.getPropertyExtensionDescriptor(extension, name);
            // Checks the entity scope
            if (propertyExtensionDescriptor.getScope().contains(entity)) {
                throw new PropertyScopeException(extension, name, entity);
            }
            // Validates the value
            propertyExtensionDescriptor.validate(value);
            // Saves the value
            // FIXME Uses a DAO
        }
    }

}
