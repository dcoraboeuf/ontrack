package net.ontrack.core.model;

import lombok.Data;

import java.util.List;

@Data
public class BranchBuilds {

    private final List<ValidationStampSummary> validationStamps;
    private final List<PromotionLevelSummary> promotionLevels;
    private final List<Status> statusList;
    private final List<BuildCompleteStatus> builds;

}
