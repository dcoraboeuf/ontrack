package net.ontrack.extension.svn.dao.model;

import lombok.Data;
import net.ontrack.extension.svn.support.SVNUtils;
import org.tmatesoft.svn.core.SVNURL;

@Data
public class TRepository {

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
    private final String issueServiceName;
    private final Integer issueServiceConfigId;


    public SVNURL getSVNURL() {
        return SVNUtils.toURL(url);
    }
}
