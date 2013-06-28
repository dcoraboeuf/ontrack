package net.ontrack.extension.git.model;

import lombok.Data;
import net.ontrack.core.model.BranchPromotions;
import net.ontrack.core.model.BuildInfo;

import java.util.Collection;
import java.util.List;

@Data
public class GitCommitInfo {

    private final GitUICommit commit;
    private final Collection<BuildInfo> builds;
    private final List<BranchPromotions> promotionsPerBranch;

}
