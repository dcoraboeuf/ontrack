package net.ontrack.extension.svnexplorer.ui;

import net.ontrack.extension.svnexplorer.model.ChangeLogRequest;
import net.ontrack.extension.svnexplorer.model.ChangeLogSummary;

public interface SVNExplorerUI {

    ChangeLogSummary getChangeLogSummary(ChangeLogRequest request);

}
