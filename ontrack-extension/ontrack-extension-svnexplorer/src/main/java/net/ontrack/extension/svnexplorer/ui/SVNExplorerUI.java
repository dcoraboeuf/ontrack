package net.ontrack.extension.svnexplorer.ui;

import net.ontrack.extension.svnexplorer.model.*;

import java.util.Locale;

public interface SVNExplorerUI {

    ChangeLogSummary getChangeLogSummary(Locale locale, ChangeLogRequest request);

    ChangeLogRevisions getChangeLogRevisions(String uuid);

    ChangeLogIssues getChangeLogIssues(String uuid);

    ChangeLogFiles getChangeLogFiles(String uuid);

    ChangeLogInfo getChangeLogInfo(String uuid);

    RevisionInfo getRevisionInfo(Locale locale, int repositoryId, long revision);

    IssueInfo getIssueInfo(Locale locale, int repositoryId, String key);

    BranchHistory getBranchHistory(Locale locale, String projectName);

    boolean isChangeLogAvailable(int branchId);
}
