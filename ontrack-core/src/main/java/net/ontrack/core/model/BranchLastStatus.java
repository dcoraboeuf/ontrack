package net.ontrack.core.model;

import lombok.Data;

import java.util.List;

@Data
public class BranchLastStatus {

    private final BranchSummary branch;
    private final BuildSummary latestBuild;
    private final List<Promotion> promotions;

}
