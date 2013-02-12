package net.ontrack.core.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BuildCompleteStatus {

    // Build general info
    private final int id;
    private final String name;
    private final String description;

    // List of validation stamps with their associated runs for this build
    private final List<BuildValidationStamp> validationStamps;

    public BuildCompleteStatus (BuildSummary summary, List<BuildValidationStamp> stamps) {
        this(summary.getId(), summary.getName(), summary.getDescription(), stamps);
    }

}
