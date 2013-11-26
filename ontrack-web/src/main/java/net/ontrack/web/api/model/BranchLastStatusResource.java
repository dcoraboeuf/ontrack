package net.ontrack.web.api.model;

import lombok.Data;

import java.util.List;

@Data
public class BranchLastStatusResource extends AbstractResource<BranchLastStatusResource> {
    private final BranchResource branch;
    private final BuildResource latestBuild;
    private final List<PromotionResource> promotions;

}
