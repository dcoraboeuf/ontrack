package net.ontrack.core.model;

import lombok.Data;

import java.util.List;

@Data
public class DashboardBranchAdmin {

    private final BranchSummary branch;
    private final List<FlaggedValidationStamp> stamps;

}
