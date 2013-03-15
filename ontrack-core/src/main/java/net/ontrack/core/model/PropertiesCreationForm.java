package net.ontrack.core.model;

import lombok.Data;
import net.ontrack.core.support.ListUtils;

import java.util.Collections;
import java.util.List;

/**
 * Defines the creation of zero or more properties for an item.
 */
@Data
public class PropertiesCreationForm {

    public static PropertiesCreationForm create() {
        return new PropertiesCreationForm(Collections.<PropertyCreationForm>emptyList());
    }

    private final List<PropertyCreationForm> list;

    public PropertiesCreationForm with(PropertyCreationForm propertyCreationForm) {
        return new PropertiesCreationForm(ListUtils.concat(this.list, propertyCreationForm));
    }
}
