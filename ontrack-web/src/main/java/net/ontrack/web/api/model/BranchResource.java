package net.ontrack.web.api.model;

import com.google.common.base.Function;
import lombok.Data;
import net.ontrack.core.model.BranchSummary;

@Data
public class BranchResource extends AbstractResource<BranchResource> {

    public static Function<BranchSummary, BranchResource> summary = new Function<BranchSummary, BranchResource>() {
        @Override
        public BranchResource apply(BranchSummary o) {
            return new BranchResource(
                    o.getId(),
                    o.getProject().getName(),
                    o.getName(),
                    o.getDescription()
            );
            // TODO Branch view
            // TODO Branch self
        }
    };
    private final int id;
    private final String project;
    private final String name;
    private final String description;

}
