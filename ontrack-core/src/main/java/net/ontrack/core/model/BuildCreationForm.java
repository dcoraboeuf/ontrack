package net.ontrack.core.model;

import net.ontrack.core.validation.NameDescription;
import lombok.Data;

@Data
public class BuildCreationForm implements NameDescription {
	
	private final String name;
	private final String description;
    private final PropertiesCreationForm properties;

}
