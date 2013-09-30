package net.ontrack.core.model;

import lombok.Data;

@Data
public class BranchBuildValidationStampLastStatus {

    private final int validationStampId;
    private final String validationStampName;
    private final BranchBuildLastValidationRun lastValidationRun;

}
