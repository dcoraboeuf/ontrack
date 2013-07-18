package net.ontrack.web.hateoas;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.ontrack.core.model.BranchSummary;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BranchResource extends AbstractResource<BranchResource> {

    private final int branchId;
    private final String projectName;
    private final String name;
    private final String description;

    public BranchResource(BranchSummary o) {
        this(o.getId(), o.getProject().getName(), o.getName(), o.getDescription());
    }
}
