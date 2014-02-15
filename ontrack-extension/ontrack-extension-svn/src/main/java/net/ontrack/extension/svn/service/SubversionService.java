package net.ontrack.extension.svn.service;

import net.ontrack.core.model.Ack;
import net.ontrack.extension.svn.service.model.*;
import org.joda.time.DateTime;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.util.Collection;
import java.util.List;

public interface SubversionService {

    /**
     * Gets the list of defined repository configurations
     */
    List<SVNRepository> getAllRepositories();

    /**
     * Creates a new repository configuration
     */
    SVNRepository createRepository(SVNRepositoryForm form);

    /**
     * Updates a repository configuration
     */
    SVNRepository updateRepository(int id, SVNRepositoryForm form);

    /**
     * Gets a repository configuration using its ID.
     */
    SVNRepository getRepository(int id);

    /**
     * Deletes a repository configuration.
     */
    Ack deleteRepository(int id);

    /**
     * Gets the absolute URL that for a path in the subversion repository
     */
    String getURL(SVNRepository repository, String path);

    /**
     * Gets the browsing URL that for a path in the subversion repository
     */
    String getBrowsingURL(SVNRepository repository, String path);

    /**
     * Gets the latest revision for the URL
     */
    long getRepositoryRevision(SVNRepository repository, SVNURL url);

    SVNReference getReference(SVNRepository repository, SVNLocation location);

    void log(SVNRepository repository, SVNURL url, SVNRevision pegRevision, SVNRevision startRevision, SVNRevision stopRevision,
             boolean stopOnCopy, boolean discoverChangedPaths, long limit, boolean includeMergedRevisions,
             ISVNLogEntryHandler isvnLogEntryHandler);

    boolean isTrunkOrBranch(SVNRepository repository, String path);

    List<Long> getMergedRevisions(SVNRepository repository, SVNURL svnurl, long revision);

    List<Long> getMergesForRevision(SVNRepository repository, long revision);

    boolean exists(SVNRepository repository, SVNURL url, SVNRevision revision);

    boolean isTagOrBranch(SVNRepository repository, String path);

    boolean isTag(SVNRepository repository, String path);

    /**
     * Gets the Subversion history from a path
     *
     * @param path Path to start the history from. This path will be included
     *             as the first item in the history.
     * @return History of copy events. Never null and will at least contain the information
     *         for the given <code>path</code>.
     */
    SVNHistory getHistory(int repositoryId, String path);

    /**
     * Gets the URL that allows a user to browse the content of a revision
     */
    String getRevisionBrowsingURL(SVNRepository repository, long revision);

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
    SVNRevisionInfo getRevisionInfo(SVNRepository repository, long revision);

    /**
     * Formats a date as ISO for a revision
     */
    String formatRevisionTime(DateTime time);

    /**
     * Gets the list of changes for a revision
     */
    SVNRevisionPaths getRevisionPaths(SVNRepository repository, long revision);

    /**
     * Gets the URL that allows to browse for one changeset on a path
     */
    String getFileChangeBrowsingURL(SVNRepository repository, String path, long revision);

    Collection<SVNLocation> getCopiesFrom(SVNRepository repository, SVNLocation location, SVNLocationSortMode sortMode);

    Collection<SVNLocation> getCopiesFromBefore(SVNRepository repository, SVNLocation location, SVNLocationSortMode sortMode);

    boolean isIndexedIssue(String key);

    List<Long> getRevisionsForIssueKey(String key);

    boolean isClosed(SVNRepository repository, String path);

    SVNLocation getFirstCopyAfter(SVNRepository repository, SVNLocation location);
}
