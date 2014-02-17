package net.ontrack.extension.svn.service.model;

import lombok.Data;
import net.ontrack.extension.issue.IssueServiceConfigSummary;
import net.ontrack.extension.issue.IssueServiceSummary;

// FIXME The password must be removed, and the update service must be updated
// in order to update the password only if filled in
@Data
public class SVNRepositorySummary {

    private final int id;
    private final String name;
    private final String url;
    private final String user;
    private final String password;
    private final String branchPattern;
    private final String tagPattern;
    private final String tagFilterPattern;
    private final String browserForPath;
    private final String browserForRevision;
    private final String browserForChange;
    private final int indexationInterval;
    private final long indexationStart;
    private final IssueServiceSummary issueService;
    private final IssueServiceConfigSummary issueServiceConfig;

}
