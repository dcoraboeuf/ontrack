package net.ontrack.core.ui;

import net.ontrack.core.model.*;

import java.util.List;
import java.util.Locale;

public interface PropertyUI {

    Ack saveProperty(
            Entity entity,
            int entityId,
            String extension,
            String name,
            PropertyForm form);

    List<DisplayablePropertyValue> getProperties(
            Locale locale,
            Entity entity,
            int entityId);

    List<DisplayableProperty> getPropertyList(
            Locale locale,
            Entity entity);

    List<EditableProperty> getEditableProperties(
            Locale locale,
            Entity entity,
            int entityId);

    String getPropertyValue(
            Entity entity,
            int entityId,
            String extension,
            String name
    );

    EntityStubCollection getEntitiesForPropertyValue(
            Entity entity,
            PropertyValue propertyValue
    );

}
