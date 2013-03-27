package net.ontrack.extension.svnexplorer.service;

import net.ontrack.extension.svnexplorer.model.ChangeLogSummary;

public interface SVNExplorerService {

    ChangeLogSummary getChangeLogSummary(int branch, int from, int to);
}
