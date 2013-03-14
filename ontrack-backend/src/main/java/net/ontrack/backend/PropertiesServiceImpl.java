package net.ontrack.backend;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.ontrack.backend.dao.PropertyDao;
import net.ontrack.backend.dao.model.TProperty;
import net.ontrack.core.model.Entity;
import net.ontrack.core.model.PropertiesCreationForm;
import net.ontrack.core.model.PropertyCreationForm;
import net.ontrack.core.model.PropertyValue;
import net.ontrack.extension.api.PropertyExtensionDescriptor;
import net.ontrack.extension.api.PropertyExtensionManager;
import net.ontrack.extension.api.PropertyExtensionNotFoundException;
import net.ontrack.service.PropertiesService;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PropertiesServiceImpl implements PropertiesService {

    private final PropertyExtensionManager propertyExtensionManager;
    private final PropertyDao propertyDao;

    @Autowired
    public PropertiesServiceImpl(PropertyExtensionManager propertyExtensionManager, PropertyDao propertyDao) {
        this.propertyExtensionManager = propertyExtensionManager;
        this.propertyDao = propertyDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropertyValue> getPropertyValues(Entity entity, int entityId) {
        return Lists.transform(
                propertyDao.findAll(entity, entityId),
                new Function<TProperty, PropertyValue>() {
                    @Override
                    public PropertyValue apply(TProperty p) {
                        return new PropertyValue(
                                p.getExtension(),
                                p.getName(),
                                p.getValue()
                        );
                    }
                }
        );
    }

    @Override
    @Transactional(readOnly = true)
    public String getPropertyValue(Entity entity, int entityId, String extension, String name) {
        TProperty p = propertyDao.findByExtensionAndName(entity, entityId, extension, name);
        return p != null ? p.getValue() : null;
    }

    @Override
    public String toHTML(String extension, String name, String value) {
        try {
            PropertyExtensionDescriptor descriptor = propertyExtensionManager.getPropertyExtensionDescriptor(extension, name);
            return descriptor.toHTML(value);
        } catch (PropertyExtensionNotFoundException e) {
            return StringEscapeUtils.escapeHtml4(value);
        }
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
            if (!propertyExtensionDescriptor.getScope().contains(entity)) {
                throw new PropertyScopeException(extension, name, entity);
            }
            // Validates the value
            propertyExtensionDescriptor.validate(value);
            // Saves the value
            propertyDao.saveProperty(entity, entityId, extension, name, value);
        }
    }

}
