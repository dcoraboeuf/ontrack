package net.ontrack.core.model;

import lombok.Data;

import java.util.List;

@Data
public class BranchFilterData {

    private final List<PromotionLevelSummary> promotionLevels;
    private final List<ValidationStampSummary> validationStamps;
    private final List<Status> statuses;
    private final List<DisplayableProperty> properties;

}
