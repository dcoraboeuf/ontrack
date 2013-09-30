package net.ontrack.core.model;

import lombok.Data;

@Data
public class BranchBuildLastValidationRun {

    private final int validationRunId;
    private final int validationRunOrder;
    private final DatedSignature datedSignature;
    private final BranchBuildLastValidationRunStatus lastValidationRunStatus;

}
