package net.ontrack.core.model;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class FlaggedPromotionLevel {

    public static final Function<? super PromotionLevelSummary, ? extends FlaggedPromotionLevel> UNFLAGGED = new Function<PromotionLevelSummary, FlaggedPromotionLevel>() {
        @Override
        public FlaggedPromotionLevel apply(PromotionLevelSummary summary) {
            return new FlaggedPromotionLevel(summary);
        }
    };

    private final PromotionLevelSummary summary;
    private final boolean flag;

    public FlaggedPromotionLevel(PromotionLevelSummary summary) {
        this(summary, false);
    }

    public FlaggedPromotionLevel select() {
        return new FlaggedPromotionLevel(summary, true);
    }

    public static Function<? super FlaggedPromotionLevel, ? extends FlaggedPromotionLevel> selectFn(final Predicate<PromotionLevelSummary> predicate) {
        return new Function<FlaggedPromotionLevel, FlaggedPromotionLevel>() {
            @Override
            public FlaggedPromotionLevel apply(FlaggedPromotionLevel input) {
                if (predicate.apply(input.getSummary())) {
                    return input.select();
                } else {
                    return input;
                }
            }
        };
    }
}
