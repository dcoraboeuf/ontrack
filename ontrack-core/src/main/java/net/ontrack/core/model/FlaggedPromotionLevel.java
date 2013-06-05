package net.ontrack.core.model;

import com.google.common.base.Function;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FlaggedPromotionLevel {

    public static final Function<? super PromotionLevelSummary, ? extends FlaggedPromotionLevel> UNFLAGGED = new Function<PromotionLevelSummary, FlaggedPromotionLevel>() {
        @Override
        public FlaggedPromotionLevel apply(PromotionLevelSummary summary) {
            return new FlaggedPromotionLevel(summary);
        }
    };

    private final PromotionLevelSummary summary;
    private boolean flag;

    public FlaggedPromotionLevel(PromotionLevelSummary summary) {
        this(summary, false);
    }

    public FlaggedPromotionLevel select() {
        this.flag = true;
        return this;
    }

}
