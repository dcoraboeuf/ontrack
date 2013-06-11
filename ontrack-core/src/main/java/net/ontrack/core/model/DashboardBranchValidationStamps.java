package net.ontrack.core.model;

import lombok.Data;

import java.util.List;

@Data
public class DashboardBranchValidationStamps {

    private final List<ValidationStampStatus> passed;
    private final List<ValidationStampStatus> failed;
    private final List<ValidationStampStatus> notRuns;

}
