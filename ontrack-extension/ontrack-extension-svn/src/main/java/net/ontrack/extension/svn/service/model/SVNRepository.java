package net.ontrack.extension.svn.service.model;

import com.google.common.base.Function;
import lombok.Data;
import net.ontrack.extension.issue.IssueService;
import net.ontrack.extension.issue.IssueServiceConfig;
import net.ontrack.extension.svn.support.SVNUtils;
import org.tmatesoft.svn.core.SVNURL;

@Data
public class SVNRepository {

    public static final Function<SVNRepository, SVNRepositorySummary> summaryFn = new Function<SVNRepository, SVNRepositorySummary>() {
        @Override
        public SVNRepositorySummary apply(SVNRepository o) {
            return new SVNRepositorySummary(
                    o.getId(),
                    o.getName(),
                    o.getUrl(),
                    o.getBranchPattern(),
                    o.getTagPattern(),
                    o.getTagFilterPattern(),
                    o.getBrowserForPath(),
                    o.getBrowserForRevision(),
                    o.getBrowserForChange(),
                    o.getIndexationInterval(),
                    o.getIndexationStart(),
                    IssueService.summaryFn.apply(o.getIssueService()),
                    IssueServiceConfig.summaryFn.apply(o.getIssueServiceConfig())
            );
        }
    };

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
    private final IssueService issueService;
    private final IssueServiceConfig issueServiceConfig;


    public SVNURL getSVNURL() {
        return SVNUtils.toURL(url);
    }
}
