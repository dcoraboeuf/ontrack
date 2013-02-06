package net.ontrack.core.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BuildValidationStamp {

    public static BuildValidationStamp of (ValidationStampSummary validationStamp) {
        return new BuildValidationStamp(validationStamp.getName(), validationStamp.getDescription(), Collections.<BuildValidationStampRun>emptyList());
    }

	private final String name;
	private final String description;
    private final List<BuildValidationStampRun> runs;

    public boolean isRun() {
        return !runs.isEmpty();
    }

    public BuildValidationStamp withRuns (List<BuildValidationStampRun> runs) {
        return new BuildValidationStamp(name, description, runs);
    }

}
