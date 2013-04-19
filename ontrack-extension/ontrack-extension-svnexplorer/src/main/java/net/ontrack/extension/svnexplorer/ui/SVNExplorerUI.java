package net.ontrack.extension.svnexplorer.ui;

import net.ontrack.extension.svnexplorer.model.*;

import java.util.Locale;

public interface SVNExplorerUI {

    ChangeLogSummary getChangeLogSummary(ChangeLogRequest request);

    ChangeLogRevisions getChangeLogRevisions(String uuid);

    ChangeLogIssues getChangeLogIssues(String uuid);

    ChangeLogFiles getChangeLogFiles(String uuid);

    ChangeLogInfo getChangeLogInfo(String uuid);

    RevisionInfo getRevisionInfo(Locale locale, long revision);
}
