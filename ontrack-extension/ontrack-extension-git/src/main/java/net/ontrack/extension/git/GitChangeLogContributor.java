package net.ontrack.extension.git;

import net.ontrack.core.model.BranchSummary;

public interface GitChangeLogContributor {

    boolean isApplicable(BranchSummary branch);

    GitChangeLogExtension getExtension(BranchSummary branch);
}
