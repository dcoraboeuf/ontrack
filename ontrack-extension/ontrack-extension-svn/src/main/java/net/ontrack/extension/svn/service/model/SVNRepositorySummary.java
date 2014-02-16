package net.ontrack.extension.svn.service.model;

import lombok.Data;
import net.ontrack.extension.issue.IssueServiceConfigSummary;
import net.ontrack.extension.issue.IssueServiceSummary;

@Data
public class SVNRepositorySummary {

    private final int id;
    private final String name;
    private final String url;
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
