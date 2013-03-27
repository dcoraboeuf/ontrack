package net.ontrack.extension.svn;

import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.util.List;

public interface SubversionService {

    /**
     * Gets the absolute URL that for a path in the subversion repository
     */
    String getURL(String path);

    /**
     * Gets the browsing URL that for a path in the subversion repository
     */
    String getBrowsingURL(String path);

    /**
     * Gets the latest revision for the URL
     */
    long getRepositoryRevision(SVNURL url);

    void log(SVNURL url, SVNRevision pegRevision, SVNRevision startRevision, SVNRevision stopRevision,
             boolean stopOnCopy, boolean discoverChangedPaths, long limit, boolean includeMergedRevisions,
             ISVNLogEntryHandler isvnLogEntryHandler);

    boolean isTrunkOrBranch(String path);

    List<Long> getMergedRevisions(SVNURL svnurl, long revision);

    boolean exists(SVNURL url, SVNRevision revision);

    boolean isTagOrBranch(String path);
}
