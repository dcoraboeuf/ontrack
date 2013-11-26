package net.ontrack.web.api.assembly;

import com.google.common.base.Function;
import net.ontrack.core.model.Promotion;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.web.api.model.PromotionResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static net.ontrack.core.support.FunctionsUtils.optional;

@Component
public class PromotionAssemblerImpl extends AbstractAssembler implements PromotionAssembler {

    private final BuildAssembler buildAssembler;
    private final PromotionLevelAssembler promotionLevelAssembler;

    @Autowired
    public PromotionAssemblerImpl(SecurityUtils securityUtils, BuildAssembler buildAssembler, PromotionLevelAssembler promotionLevelAssembler) {
        super(securityUtils);
        this.buildAssembler = buildAssembler;
        this.promotionLevelAssembler = promotionLevelAssembler;
    }

    @Override
    public Function<Promotion, PromotionResource> summary() {
        return new Function<Promotion, PromotionResource>() {
            @Override
            public PromotionResource apply(Promotion o) {
                return new PromotionResource(
                        promotionLevelAssembler.summary().apply(o.getPromotionLevel()),
                        optional(buildAssembler.summary()).apply(o.getBuildSummary()),
                        o.getSignature()
                );
            }
        };
    }
}
