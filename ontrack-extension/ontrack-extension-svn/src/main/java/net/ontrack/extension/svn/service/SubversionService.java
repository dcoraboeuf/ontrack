package net.ontrack.extension.svn.service;

import net.ontrack.extension.svn.service.model.*;
import org.joda.time.DateTime;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.util.Collection;
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

    SVNReference getReference(SVNLocation location);

    void log(SVNURL url, SVNRevision pegRevision, SVNRevision startRevision, SVNRevision stopRevision,
             boolean stopOnCopy, boolean discoverChangedPaths, long limit, boolean includeMergedRevisions,
             ISVNLogEntryHandler isvnLogEntryHandler);

    boolean isTrunkOrBranch(String path);

    List<Long> getMergedRevisions(SVNURL svnurl, long revision);

    boolean exists(SVNURL url, SVNRevision revision);

    boolean isTagOrBranch(String path);

    boolean isTag(String path);

    /**
     * Gets the Subversion history from a path
     *
     * @param path Path to start the history from. This path will be included
     *             as the first item in the history.
     * @return History of copy events. Never null and will at least contain the information
     *         for the given <code>path</code>.
     */
    SVNHistory getHistory(String path);

    /**
     * Gets the URL that allows a user to browse the content of a revision
     */
    String getRevisionBrowsingURL(long revision);

    /**
     * Returns the list of indexed issues for a given revision
     *
     * @param revision Revision to get the issue keys for
     * @return List of keys, never <code>null</code>
     */
    List<String> getIssueKeysForRevision(long revision);

    /**
     * Gets the information about a revision
     *
     * @param revision Revision to get information about
     * @return Full details about this revision
     */
    SVNRevisionInfo getRevisionInfo(long revision);

    /**
     * Formats a date as ISO for a revision
     */
    String formatRevisionTime(DateTime time);

    /**
     * Gets the list of changes for a revision
     */
    SVNRevisionPaths getRevisionPaths(long revision);

    /**
     * Gets the URL that allows to browse for one changeset on a path
     */
    String getFileChangeBrowsingURL(String path, long revision);

    Collection<SVNLocation> getCopiesFrom(SVNLocation location, SVNLocationSortMode sortMode);

    boolean isIndexedIssue(String key);

    List<Long> getRevisionsForIssueKey(String key);

    void onEvents(SVNEventCallback svnEventCallback);
}
