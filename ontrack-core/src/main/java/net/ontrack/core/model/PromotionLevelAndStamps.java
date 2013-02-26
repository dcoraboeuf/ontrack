package net.ontrack.core.model;

import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.ontrack.core.support.ListUtils;

import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PromotionLevelAndStamps {

    private final PromotionLevelSummary promotionLevel;
    private final List<ValidationStampSummary> validationStamps;

    public PromotionLevelAndStamps(PromotionLevelSummary promotionLevel) {
        this.promotionLevel = promotionLevel;
        this.validationStamps = Collections.emptyList();
    }

    public PromotionLevelAndStamps withStamp(ValidationStampSummary stamp) {
        return new PromotionLevelAndStamps(
                this.promotionLevel,
                ListUtils.concat(validationStamps, stamp)
        );
    }

    public PromotionLevelAndStamps withStamps(List<ValidationStampSummary> stamps) {
        return new PromotionLevelAndStamps(promotionLevel, ImmutableList.copyOf(stamps));
    }
}
