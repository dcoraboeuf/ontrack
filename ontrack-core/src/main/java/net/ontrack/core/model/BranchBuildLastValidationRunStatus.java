package net.ontrack.core.model;

import lombok.Data;

@Data
public class BranchBuildLastValidationRunStatus {

    private final int validationRunStatusId;
    private final Status status;
    private final String description;

}
