package net.ontrack.core.model;

import lombok.Data;

@Data
public class ValidationRunSummary {

	private final int id;
    private final int runOrder;
	private final String description;
	private final BuildSummary build;
	private final ValidationStampSummary validationStamp;
	private final ValidationRunStatusStub validationRunStatus;

}
