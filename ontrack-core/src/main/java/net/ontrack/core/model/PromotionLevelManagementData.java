package net.ontrack.core.model;

import lombok.Data;

import java.util.List;

@Data
public class PromotionLevelManagementData {

    private final List<ValidationStampSummary> freeValidationStampList;
    private final List<PromotionLevelAndStamps> promotionLevelAndStampsList;

}
