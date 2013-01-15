package net.ontrack.core.model;

import lombok.Data;

@Data
public class ValidationRunDefinitionForm {
	
	private final ValidationRunStatus status;
	private final String description;

}
