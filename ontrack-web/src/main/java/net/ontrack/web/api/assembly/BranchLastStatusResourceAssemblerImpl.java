package net.ontrack.web.api.assembly;

import com.google.common.base.Function;
import net.ontrack.core.model.BranchLastStatus;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.web.api.model.BranchLastStatusResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.google.common.collect.Lists.transform;
import static net.ontrack.core.support.FunctionsUtils.optional;

@Component
public class BranchLastStatusResourceAssemblerImpl extends AbstractAssembler implements BranchLastStatusResourceAssembler {

    private final BranchAssembler branchAssembler;
    private final BuildAssembler buildAssembler;
    private final PromotionAssembler promotionAssembler;

    @Autowired
    public BranchLastStatusResourceAssemblerImpl(SecurityUtils securityUtils, BranchAssembler branchAssembler, BuildAssembler buildAssembler, PromotionAssembler promotionAssembler) {
        super(securityUtils);
        this.branchAssembler = branchAssembler;
        this.buildAssembler = buildAssembler;
        this.promotionAssembler = promotionAssembler;
    }

    @Override
    public Function<BranchLastStatus, BranchLastStatusResource> summary() {
        return new Function<BranchLastStatus, BranchLastStatusResource>() {
            @Override
            public BranchLastStatusResource apply(BranchLastStatus o) {
                return new BranchLastStatusResource(
                        branchAssembler.summary().apply(o.getBranch()),
                        optional(buildAssembler.summary()).apply(o.getLatestBuild()),
                        transform(
                                o.getPromotions(),
                                promotionAssembler.summary()
                        )
                );
            }
        };
    }
}
