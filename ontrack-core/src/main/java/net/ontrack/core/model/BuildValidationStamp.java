package net.ontrack.core.model;

import lombok.Data;

@Data
public class BuildValidationStamp {

	private final String name;
	private final String description;
	private final int runId;
	private final String status;
	private final String statusDescription;

}
