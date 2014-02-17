package net.ontrack.extension.svn.service.model;

import lombok.Data;
import net.ontrack.extension.issue.IssueServiceConfigSummary;
import net.ontrack.extension.issue.IssueServiceSummary;
import net.ontrack.extension.svn.support.SVNUtils;
import org.tmatesoft.svn.core.SVNURL;

@Data
public class SVNRepository {

    private final int id;
    private final String name;
    private final String url;
    private final String user;
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

    public SVNURL getSVNURL() {
        return SVNUtils.toURL(url);
    }

    public String getIssueServiceName() {
        return issueService != null
                ? issueService.getId()
                : null;
    }

    public Integer getIssueServiceConfigId() {
        return issueServiceConfig != null
                ? issueServiceConfig.getId()
                : null;
    }
}
