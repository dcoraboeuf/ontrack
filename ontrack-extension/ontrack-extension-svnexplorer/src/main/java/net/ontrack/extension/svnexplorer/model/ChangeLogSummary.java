package net.ontrack.extension.svnexplorer.model;

import lombok.Data;
import net.ontrack.core.model.BranchSummary;

@Data
public class ChangeLogSummary {

    private final BranchSummary branch;
    private final SVNBuild buildFrom;
    private final SVNBuild buildTo;

}
