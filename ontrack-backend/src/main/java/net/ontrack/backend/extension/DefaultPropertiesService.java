package net.ontrack.backend.extension;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import net.ontrack.backend.PropertyScopeException;
import net.ontrack.backend.dao.PropertyDao;
import net.ontrack.backend.dao.model.TProperty;
import net.ontrack.core.model.*;
import net.ontrack.extension.api.ExtensionManager;
import net.ontrack.extension.api.property.PropertiesService;
import net.ontrack.extension.api.property.PropertyExtensionDescriptor;
import net.ontrack.extension.api.property.PropertyExtensionNotFoundException;
import net.ontrack.extension.api.property.PropertyValueWithDescriptor;
import net.sf.jstring.Strings;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Service
public class DefaultPropertiesService implements PropertiesService {

    private final ExtensionManager extensionManager;
    private final PropertyDao propertyDao;

    @Autowired
    public DefaultPropertiesService(ExtensionManager extensionManager, PropertyDao propertyDao) {
        this.extensionManager = extensionManager;
        this.propertyDao = propertyDao;
    }

    @Override
    public List<? extends PropertyExtensionDescriptor> getProperties(Entity entity) {
        return extensionManager.getPropertyExtensionDescriptors(entity);
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
    public List<PropertyValueWithDescriptor> getPropertyValuesWithDescriptor(Entity entity, int entityId) {
        return Lists.newArrayList(
                Collections2.filter(
                        Collections2.transform(
                                getPropertyValues(entity, entityId),
                                new Function<PropertyValue, PropertyValueWithDescriptor>() {
                                    @Override
                                    public PropertyValueWithDescriptor apply(PropertyValue value) {
                                        PropertyExtensionDescriptor propertyExtensionDescriptor;
                                        try {
                                            propertyExtensionDescriptor = extensionManager.getPropertyExtensionDescriptor(
                                                    value.getExtension(),
                                                    value.getName());
                                        } catch (PropertyExtensionNotFoundException ex) {
                                            propertyExtensionDescriptor = null;
                                        }
                                        return new PropertyValueWithDescriptor(
                                                propertyExtensionDescriptor,
                                                value.getValue()
                                        );
                                    }
                                }
                        ),
                        new Predicate<PropertyValueWithDescriptor>() {
                            @Override
                            public boolean apply(PropertyValueWithDescriptor it) {
                                return it.getDescriptor() != null;
                            }
                        }
                )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public String getPropertyValue(Entity entity, int entityId, String extension, String name) {
        TProperty p = propertyDao.findByExtensionAndName(entity, entityId, extension, name);
        return p != null ? p.getValue() : null;
    }

    @Override
    public String toHTML(Strings strings, Locale locale, String extension, String name, String value) {
        try {
            PropertyExtensionDescriptor descriptor = extensionManager.getPropertyExtensionDescriptor(extension, name);
            return descriptor.toHTML(strings, locale, value);
        } catch (PropertyExtensionNotFoundException e) {
            return StringEscapeUtils.escapeHtml4(value);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String editHTML(Strings strings, Locale locale, Entity entity, int entityId, String extension, String name) {
        // Gets the property value
        String value = getPropertyValue(entity, entityId, extension, name);
        // Gets the descriptor for this property
        PropertyExtensionDescriptor descriptor = extensionManager.getPropertyExtensionDescriptor(extension, name);
        // OK
        return descriptor.editHTML(strings, locale, value);
    }

    @Override
    @Transactional
    public Ack saveProperty(Entity entity, int entityId, String extension, String name, String value) {
        createProperties(
                entity,
                entityId,
                new PropertiesCreationForm(
                        Collections.singletonList(
                                new PropertyCreationForm(
                                        extension,
                                        name,
                                        value
                                )
                        )
                )
        );
        return Ack.OK;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Integer> findEntityByPropertyValue(Entity entity, String extension, String name, String value) {
        return propertyDao.findEntityByPropertyValue(entity, extension, name, value);
    }

    @Override
    @Transactional
    public void createProperties(Entity entity, int entityId, PropertiesCreationForm properties) {
        // For all properties
        if (properties != null) {
            for (PropertyCreationForm propertyCreationForm : properties.getList()) {
                String extension = propertyCreationForm.getExtension();
                String name = propertyCreationForm.getName();
                String value = propertyCreationForm.getValue();
                // Gets the property extension descriptor
                PropertyExtensionDescriptor propertyExtensionDescriptor = extensionManager.getPropertyExtensionDescriptor(extension, name);
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

}
