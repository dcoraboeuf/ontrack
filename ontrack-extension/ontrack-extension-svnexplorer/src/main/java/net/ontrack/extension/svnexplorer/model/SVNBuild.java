package net.ontrack.extension.svnexplorer.model;

import lombok.Data;
import net.ontrack.core.model.BuildSummary;
import net.ontrack.extension.svn.service.model.SVNHistory;

@Data
public class SVNBuild {

    // TODO Promotion levels
    // TODO Validation stamps

    private final BuildSummary buildSummary;
    private final SVNHistory history;

}
