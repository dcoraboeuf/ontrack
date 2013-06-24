package net.ontrack.extension.git.model;

import lombok.Data;
import net.ontrack.core.model.BranchSummary;

@Data
public class ChangeLogSummary {

    private final String uuid;
    private final BranchSummary branch;
    private final ChangeLogBuild buildFrom;
    private final ChangeLogBuild buildTo;

}
