package net.ontrack.core.model;

import lombok.Data;

@Data
public class ValidationRunSummary {

	private final int id;
	private final String name;
	private final String description;
	private final BuildSummary build;
	private final ValidationStampSummary validationStamp;

}
