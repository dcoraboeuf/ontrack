package net.ontrack.web.api.model;

import com.google.common.base.Function;
import lombok.Data;
import net.ontrack.core.model.PromotionLevelSummary;

@Data
public class PromotionLevelResource extends AbstractResource<PromotionLevelResource> {

    public static Function<PromotionLevelSummary, PromotionLevelResource> summary = new Function<PromotionLevelSummary, PromotionLevelResource>() {
        @Override
        public PromotionLevelResource apply(PromotionLevelSummary o) {
            return new PromotionLevelResource(
                    o.getId(),
                    o.getBranch().getProject().getName(),
                    o.getBranch().getName(),
                    o.getLevelNb(),
                    o.getName(),
                    o.getDescription(),
                    o.isAutoPromote()
            );
        }
    };
    private final int id;
    private final String project;
    private final String branch;
    private final int levelNb;
    private final String name;
    private final String description;
    private final boolean autoPromote;

}
