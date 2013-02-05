package net.ontrack.core.model;

import lombok.Data;

@Data
public class ValidationRunStatusStub {
	
	private final int id;
	private final Status status;
	private final String description;

}
