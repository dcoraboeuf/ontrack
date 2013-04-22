package net.ontrack.core.model;

import lombok.Data;
import net.ontrack.core.model.BuildSummary;
import net.ontrack.core.model.DatedSignature;
import net.ontrack.core.model.PromotionLevelSummary;

@Data
public class Promotion {

    private final PromotionLevelSummary promotionLevel;
    private final BuildSummary buildSummary;
    private final DatedSignature signature;

}
