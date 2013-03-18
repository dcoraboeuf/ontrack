package net.ontrack.extension.api;

import net.ontrack.core.model.Entity;
import net.ontrack.core.model.PropertiesCreationForm;
import net.ontrack.core.model.PropertyValue;
import net.sf.jstring.Strings;

import java.util.List;
import java.util.Locale;

public interface PropertiesService {

    void createProperties(Entity entity, int entityId, PropertiesCreationForm properties);

    List<PropertyValue> getPropertyValues(Entity entity, int entityId);

    List<PropertyValueWithDescriptor> getPropertyValuesWithDescriptor(Entity entity, int entityId);

    String getPropertyValue(Entity entity, int entityId, String extension, String name);

    /**
     * Renders HTML for a property value
     */
    String toHTML(Strings strings, Locale locale, String extension, String name, String value);
}
