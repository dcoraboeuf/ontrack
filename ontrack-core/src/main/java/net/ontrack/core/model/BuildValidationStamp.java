package net.ontrack.core.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BuildValidationStamp {

    public static BuildValidationStamp of (ValidationStampSummary validationStamp) {
        return new BuildValidationStamp(validationStamp.getName(), validationStamp.getDescription(), 0, null, null);
    }

	private final String name;
	private final String description;
	private final int runId;
	private final String status;
	private final String statusDescription;

    public boolean isRun() {
        return runId > 0;
    }

    public BuildValidationStamp withRun (ValidationRunStatusStub validationRun) {
        return new BuildValidationStamp(name, description, validationRun.getId(), validationRun.getStatus(), validationRun.getDescription());
    }

}
