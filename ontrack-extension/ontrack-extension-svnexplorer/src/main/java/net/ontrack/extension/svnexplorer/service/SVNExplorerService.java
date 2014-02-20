package net.ontrack.extension.svnexplorer.service;

import net.ontrack.extension.svnexplorer.model.*;

import java.util.Locale;

public interface SVNExplorerService {

    ChangeLogSummary getChangeLogSummary(Locale locale, int branch, int from, int to);

    ChangeLogRevisions getChangeLogRevisions(ChangeLogSummary summary);

    ChangeLogIssues getChangeLogIssues(ChangeLogSummary summary, ChangeLogRevisions revisions);

    ChangeLogFiles getChangeLogFiles(ChangeLogSummary summary, ChangeLogRevisions revisions);

    ChangeLogInfo getChangeLogInfo(ChangeLogSummary summary, ChangeLogIssues issues, ChangeLogFiles files);

    RevisionInfo getRevisionInfo(int repositoryId, Locale locale, long revision);

    IssueInfo getIssueInfo(Locale locale, String key);

    BranchHistory getBranchHistory(int project, Locale locale);

    boolean isSvnExplorerConfigured(int branchId);
}
