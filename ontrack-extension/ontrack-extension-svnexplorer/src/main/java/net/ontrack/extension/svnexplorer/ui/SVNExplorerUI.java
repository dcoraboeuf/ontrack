package net.ontrack.extension.svnexplorer.ui;

import net.ontrack.extension.svnexplorer.model.*;

public interface SVNExplorerUI {

    ChangeLogSummary getChangeLogSummary(ChangeLogRequest request);

    ChangeLogRevisions getChangeLogRevisions(String uuid);

    ChangeLogIssues getChangeLogIssues(String uuid);

    ChangeLogFiles getChangeLogFiles(String uuid);

    ChangeLogInfo getChangeLogInfo(String uuid);

    RevisionInfo getRevisionInfo(long revision);
}
