package net.ontrack.extension.svnexplorer.model;

import lombok.Data;
import net.ontrack.core.model.BuildSummary;

import java.util.Collection;
import java.util.List;

@Data
public class RevisionInfo {

    private final ChangeLogRevision changeLogRevision;
    private final Collection<RevisionInfoBuild> builds;
    private final List<RevisionPromotions> promotionsPerBranch;

}
