package net.ontrack.extension.svnexplorer.model;

import lombok.Data;
import net.ontrack.core.model.BranchPromotions;
import net.ontrack.core.model.BuildInfo;

import java.util.Collection;
import java.util.List;

@Data
public class RevisionInfo {

    private final ChangeLogRevision changeLogRevision;
    private final Collection<BuildInfo> builds;
    private final List<BranchPromotions> promotionsPerBranch;

}
