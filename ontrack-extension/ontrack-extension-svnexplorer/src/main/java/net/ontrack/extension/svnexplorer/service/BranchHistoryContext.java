package net.ontrack.extension.svnexplorer.service;

import lombok.Data;
import net.ontrack.core.model.BranchSummary;

import java.util.List;

@Data
public class BranchHistoryContext {

    private final int projectId;
    private final List<BranchSummary> branches;

}
