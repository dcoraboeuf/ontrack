package net.ontrack.web.api.model;

import com.google.common.base.Function;
import lombok.Data;
import net.ontrack.core.model.BranchLastStatus;

import java.util.List;

import static com.google.common.collect.Lists.transform;
import static net.ontrack.core.support.FunctionsUtils.optional;

@Data
public class BranchLastStatusResource extends AbstractResource<BranchLastStatusResource> {

    public static Function<? super BranchLastStatus, ? extends BranchLastStatusResource> transform = new Function<BranchLastStatus, BranchLastStatusResource>() {
        @Override
        public BranchLastStatusResource apply(BranchLastStatus o) {
            return new BranchLastStatusResource(
                    BranchResource.summary.apply(o.getBranch()),
                    optional(BuildResource.summary).apply(o.getLatestBuild()),
                    transform(
                            o.getPromotions(),
                            PromotionResource.summary
                    )
            );
        }
    };
    private final BranchResource branch;
    private final BuildResource latestBuild;
    private final List<PromotionResource> promotions;

}
