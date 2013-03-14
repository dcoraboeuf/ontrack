package net.ontrack.service;

import net.ontrack.core.model.Entity;
import net.ontrack.core.model.PropertiesCreationForm;
import net.ontrack.core.model.PropertyValue;

import java.util.List;

public interface PropertiesService {

    void createProperties(Entity entity, int entityId, PropertiesCreationForm properties);

    List<PropertyValue> getPropertyValues(Entity entity, int entityId);

    String getPropertyValue(Entity entity, int entityId, String extension, String name);
}
