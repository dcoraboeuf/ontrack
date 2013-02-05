package net.ontrack.core.model;

import lombok.Data;

@Data
public class ValidationRunStatusSummary {
	
	private final int id;
	private final Status status;
	private final String description;

}
