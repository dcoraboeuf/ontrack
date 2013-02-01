package net.ontrack.core.model;

import lombok.Data;

@Data
public class ValidationRunCreationForm {
	
	private final ValidationRunStatus status;
	private final String description;

}
