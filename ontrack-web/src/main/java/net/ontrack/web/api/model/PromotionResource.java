package net.ontrack.web.api.model;

import com.google.common.base.Function;
import lombok.Data;
import net.ontrack.core.model.DatedSignature;
import net.ontrack.core.model.Promotion;

import static net.ontrack.core.support.FunctionsUtils.optional;

@Data
public class PromotionResource extends AbstractResource<PromotionResource> {

    public static Function<Promotion, PromotionResource> summary = new Function<Promotion, PromotionResource>() {
        @Override
        public PromotionResource apply(Promotion o) {
            return new PromotionResource(
                    PromotionLevelResource.summary.apply(o.getPromotionLevel()),
                    optional(BuildResource.summary).apply(o.getBuildSummary()),
                    o.getSignature()
            );
        }
    };
    private final PromotionLevelResource promotionLevel;
    private final BuildResource build;
    private final DatedSignature signature;

}
