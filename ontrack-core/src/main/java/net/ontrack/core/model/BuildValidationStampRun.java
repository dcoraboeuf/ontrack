package net.ontrack.core.model;

import lombok.Data;

@Data
public class BuildValidationStampRun {

	private final int runId;
    private final int runOrder;
    private final DatedSignature signature;
	private final Status status;
	private final String statusDescription;

}
