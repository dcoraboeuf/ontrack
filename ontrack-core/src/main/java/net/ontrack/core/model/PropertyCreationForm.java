package net.ontrack.core.model;

import lombok.Data;
import org.apache.commons.lang3.builder.CompareToBuilder;

/**
 * Defines the creation of a property for an item.
 */
@Data
public class PropertyCreationForm implements Comparable<PropertyCreationForm> {

    /**
     * Attached extension.
     */
    private final String extension;
    /**
     * Property name in this extension.
     */
    private final String name;
    /**
     * Value for this property
     */
    private final String value;

    @Override
    public int compareTo(PropertyCreationForm o) {
        return CompareToBuilder.reflectionCompare(this, o);
    }
}
