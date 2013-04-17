package net.ontrack.extension.svnexplorer.service;

import net.ontrack.extension.svnexplorer.model.*;

public interface SVNExplorerService {

    ChangeLogSummary getChangeLogSummary(int branch, int from, int to);

    ChangeLogRevisions getChangeLogRevisions(ChangeLogSummary summary);

    ChangeLogIssues getChangeLogIssues(ChangeLogSummary summary, ChangeLogRevisions revisions);

    ChangeLogFiles getChangeLogFiles(ChangeLogSummary summary, ChangeLogRevisions revisions);

    ChangeLogInfo getChangeLogInfo(ChangeLogSummary summary, ChangeLogIssues issues, ChangeLogFiles files);

    RevisionInfo getRevisionInfo(long revision);
}
