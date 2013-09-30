package net.ontrack.core.model;

import com.google.common.base.Function;
import lombok.Data;

@Data
public class PromotionLevelSummary {

    public static final Function<PromotionLevelSummary, PromotionLevel> toPromotionLevelFn = new Function<PromotionLevelSummary, PromotionLevel>() {
        @Override
        public PromotionLevel apply(PromotionLevelSummary o) {
            return new PromotionLevel(
                    o.getId(),
                    o.getBranch().getId(),
                    o.getLevelNb(),
                    o.getName(),
                    o.getDescription(),
                    o.isAutoPromote()
            );
        }
    };
    private final int id;
    private final BranchSummary branch;
    private final int levelNb;
    private final String name;
    private final String description;
    private final boolean autoPromote;

}
