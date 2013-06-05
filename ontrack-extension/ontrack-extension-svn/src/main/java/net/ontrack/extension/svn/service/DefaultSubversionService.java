package net.ontrack.extension.svn.service;

import com.google.common.base.Function;
import net.ontrack.extension.svn.SVNEventType;
import net.ontrack.extension.svn.SubversionConfigurationExtension;
import net.ontrack.extension.svn.dao.IssueRevisionDao;
import net.ontrack.extension.svn.dao.RevisionDao;
import net.ontrack.extension.svn.dao.SVNEventDao;
import net.ontrack.extension.svn.dao.model.TRevision;
import net.ontrack.extension.svn.dao.model.TSVNCopyEvent;
import net.ontrack.extension.svn.dao.model.TSVNEvent;
import net.ontrack.extension.svn.service.model.*;
import net.ontrack.extension.svn.support.SVNLogEntryCollector;
import net.ontrack.extension.svn.support.SVNUtils;
import net.ontrack.extension.svn.tx.SVNSession;
import net.ontrack.tx.Transaction;
import net.ontrack.tx.TransactionService;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.wc.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DefaultSubversionService implements SubversionService {

    public static final int HISTORY_MAX_DEPTH = 6;
    private final DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    private final Pattern pathWithRevision = Pattern.compile("(.*)@(\\d+)$");
    /**
     * Transforms a DAO revision into a formatted revision information object.
     */
    private final Function<TRevision, SVNRevisionInfo> revisionInfoFunction = new Function<TRevision, SVNRevisionInfo>() {
        @Override
        public SVNRevisionInfo apply(TRevision t) {
            return new SVNRevisionInfo(
                    t.getRevision(),
                    t.getAuthor(),
                    t.getCreation(),
                    t.getBranch(),
                    formatRevisionTime(t.getCreation()),
                    t.getMessage(),
                    getRevisionBrowsingURL(t.getRevision())
            );
        }
    };
    private final SubversionConfigurationExtension configurationExtension;
    private final TransactionService transactionService;
    private final SVNEventDao svnEventDao;
    private final RevisionDao revisionDao;
    private final IssueRevisionDao issueRevisionDao;

    @Autowired
    public DefaultSubversionService(SubversionConfigurationExtension configurationExtension, TransactionService transactionService, SVNEventDao svnEventDao, RevisionDao revisionDao, IssueRevisionDao issueRevisionDao) {
        this.configurationExtension = configurationExtension;
        this.transactionService = transactionService;
        this.svnEventDao = svnEventDao;
        this.revisionDao = revisionDao;
        this.issueRevisionDao = issueRevisionDao;
        SVNRepositoryFactoryImpl.setup();
        DAVRepositoryFactory.setup();
    }

    @Override
    public String formatRevisionTime(DateTime time) {
        return format.print(time);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isClosed(String path) {
        TSVNEvent lastEvent = svnEventDao.getLastEvent(path);
        return lastEvent != null && lastEvent.getType() == SVNEventType.STOP;
    }

    @Override
    @Transactional(readOnly = true)
    public SVNLocation getFirstCopyAfter(SVNLocation location) {
        return svnEventDao.getFirstCopyAfter(location);
    }

    @Override
    @Transactional(readOnly = true)
    public SVNRevisionPaths getRevisionPaths(long revision) {
        // Result
        final SVNRevisionPaths paths = new SVNRevisionPaths(getRevisionInfo(revision));
        // Gets the URL of the repository
        SVNURL rootUrl = SVNUtils.toURL(configurationExtension.getUrl());
        // Gets the diff for the revision
        try {
            getDiffClient().doDiffStatus(
                    rootUrl,
                    SVNRevision.create(revision - 1),
                    rootUrl,
                    SVNRevision.create(revision),
                    SVNDepth.INFINITY,
                    false,
                    new ISVNDiffStatusHandler() {
                        @Override
                        public void handleDiffStatus(SVNDiffStatus diffStatus) throws SVNException {
                            if (diffStatus.getKind() == SVNNodeKind.FILE) {
                                paths.addPath(
                                        new SVNRevisionPath(
                                                "/" + diffStatus.getPath(),
                                                diffStatus.getModificationType().toString()
                                        )
                                );
                            }
                        }
                    }
            );
        } catch (SVNException ex) {
            throw translateSVNException(ex);
        }
        // OK
        return paths;
    }

    @Override
    public String getFileChangeBrowsingURL(String path, long revision) {
        String browserForPathAndRevision = configurationExtension.getBrowserForChange();
        if (StringUtils.isNotBlank(browserForPathAndRevision)) {
            return browserForPathAndRevision.replace("*", path).replace("$", String.valueOf(revision));
        } else {
            return getURL(path);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<SVNLocation> getCopiesFrom(SVNLocation location, SVNLocationSortMode sortMode) {
        return svnEventDao.getCopiesFrom(location, sortMode);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<SVNLocation> getCopiesFromBefore(SVNLocation location, SVNLocationSortMode sortMode) {
        return svnEventDao.getCopiesFromBefore(location, sortMode);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isIndexedIssue(String key) {
        return issueRevisionDao.isIndexed(key);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getRevisionsForIssueKey(String key) {
        return issueRevisionDao.findRevisionsByIssue(key);
    }

    @Override
    public String getURL(String path) {
        return configurationExtension.getUrl() + path;
    }

    @Override
    public String getBrowsingURL(String path) {
        String browserForPath = configurationExtension.getBrowserForPath();
        if (StringUtils.isNotBlank(browserForPath)) {
            return browserForPath.replace("*", path);
        } else {
            return getURL(path);
        }
    }

    @Override
    public String getRevisionBrowsingURL(long revision) {
        String browserForPath = configurationExtension.getBrowserForRevision();
        if (StringUtils.isNotBlank(browserForPath)) {
            return browserForPath.replace("*", String.valueOf(revision));
        } else {
            return String.valueOf(revision);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public SVNRevisionInfo getRevisionInfo(long revision) {
        return revisionInfoFunction.apply(revisionDao.get(revision));
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getIssueKeysForRevision(long revision) {
        return issueRevisionDao.findIssuesByRevision(revision);
    }

    @Override
    public long getRepositoryRevision(SVNURL url) {
        try {
            SVNInfo info = getWCClient().doInfo(url, SVNRevision.HEAD, SVNRevision.HEAD);
            return info.getCommittedRevision().getNumber();
        } catch (SVNException e) {
            throw translateSVNException(e);
        }
    }

    @Override
    public void log(SVNURL url, SVNRevision pegRevision, SVNRevision startRevision, SVNRevision stopRevision, boolean stopOnCopy, boolean discoverChangedPaths, long limit, boolean includeMergedRevisions, ISVNLogEntryHandler isvnLogEntryHandler) {
        try {
            getLogClient().doLog(url, null, pegRevision, startRevision, stopRevision, stopOnCopy, discoverChangedPaths,
                    includeMergedRevisions, limit, null, isvnLogEntryHandler);
        } catch (SVNException e) {
            throw translateSVNException(e);
        }
    }

    @Override
    public boolean exists(SVNURL url, SVNRevision revision) {
        // Tries to gets information
        try {
            SVNInfo info = getWCClient().doInfo(url, revision, revision);
            return info != null;
        } catch (SVNException ex) {
            return false;
        }
    }

    @Override
    public List<Long> getMergedRevisions(SVNURL url, long revision) {
        // Checks that the URL exists at both R-1 and R
        SVNRevision rm1 = SVNRevision.create(revision - 1);
        SVNRevision r = SVNRevision.create(revision);
        boolean existRM1 = exists(url, rm1);
        boolean existR = exists(url, r);
        try {
            // Both revisions must be valid in order to get some merges in between
            if (existRM1 && existR) {
                // Gets the changes in merge information
                SVNDiffClient diffClient = getDiffClient();
                @SuppressWarnings("unchecked")
                Map<SVNURL, SVNMergeRangeList> before = diffClient.doGetMergedMergeInfo(url, rm1);
                @SuppressWarnings("unchecked")
                Map<SVNURL, SVNMergeRangeList> after = diffClient.doGetMergedMergeInfo(url, r);
                // Gets the difference between the two merge informations
                Map<SVNURL, SVNMergeRangeList> change;
                if (after != null && before != null) {
                    change = new HashMap<>();
                    for (Map.Entry<SVNURL, SVNMergeRangeList> entry : after.entrySet()) {
                        SVNURL source = entry.getKey();
                        SVNMergeRangeList afterMergeRangeList = entry.getValue();
                        SVNMergeRangeList beforeMergeRangeList = before.get(source);
                        if (beforeMergeRangeList != null) {
                            SVNMergeRangeList changeRangeList = afterMergeRangeList.diff(beforeMergeRangeList, false);
                            if (!changeRangeList.isEmpty()) {
                                change.put(source, changeRangeList);
                            }
                        } else {
                            change.put(source, afterMergeRangeList);
                        }
                    }
                } else {
                    change = after;
                }
                if (change == null || change.isEmpty()) {
                    return Collections.emptyList();
                } else {
                    SVNLogEntryCollector collector = new SVNLogEntryCollector();
                    for (Map.Entry<SVNURL, SVNMergeRangeList> entry : change.entrySet()) {
                        SVNURL source = entry.getKey();
                        SVNMergeRangeList mergeRangeList = entry.getValue();
                        SVNMergeRange[] mergeRanges = mergeRangeList.getRanges();
                        for (SVNMergeRange mergeRange : mergeRanges) {
                            SVNRevision endRevision = SVNRevision.create(mergeRange.getEndRevision());
                            SVNRevision startRevision = SVNRevision.create(mergeRange.getStartRevision());
                            log(source, endRevision, startRevision, endRevision, true, false, 0, false, collector);
                        }
                    }
                    List<Long> revisions = new ArrayList<>();
                    for (SVNLogEntry entry : collector.getEntries()) {
                        revisions.add(entry.getRevision());
                    }
                    return revisions;
                }
            } else {
                // One of the revisions (R-1 or R) is missing
                return Collections.emptyList();
            }
        } catch (SVNException ex) {
            throw translateSVNException(ex);
        }
    }

    @Override
    public SVNHistory getHistory(String path) {
        // Gets the reference for this first path
        SVNReference reference = getReference(path);
        // Initializes the history
        SVNHistory history = new SVNHistory();
        // Adds the initial reference if this a branch or trunk
        if (isTrunkOrBranch(reference.getPath())) {
            history = history.add(reference);
        }
        // Loops on copies
        int depth = HISTORY_MAX_DEPTH;
        while (reference != null && depth > 0) {
            depth--;
            // Gets the reference of the source
            SVNReference origin = getOrigin(reference);
            if (origin != null) {
                // Adds to the history if this a branch or trunk
                if (isTrunkOrBranch(origin.getPath())) {
                    history = history.add(origin);
                }
                // Going on
                reference = origin;
            } else {
                reference = null;
            }
        }
        // OK
        return history;
    }

    private SVNReference getOrigin(SVNReference destination) {
        // Gets the last copy event
        TSVNCopyEvent copyEvent = svnEventDao.getLastCopyEvent(destination.getPath(), destination.getRevision());
        if (copyEvent != null) {
            return getReference(copyEvent.getCopyFromPath(), SVNRevision.create(copyEvent.getCopyFromRevision()));
        } else {
            return null;
        }
    }

    private SVNReference getReference(String path) {
        Matcher matcher = pathWithRevision.matcher(path);
        if (matcher.matches()) {
            String pathOnly = matcher.group(1);
            long revision = Long.parseLong(matcher.group(2), 10);
            return getReference(pathOnly, SVNRevision.create(revision));
        } else {
            return getReference(path, SVNRevision.HEAD);
        }
    }

    private SVNReference getReference(String path, SVNRevision revision) {
        String url = getURL(path);
        SVNURL svnurl = SVNUtils.toURL(url);
        SVNInfo info = getInfo(svnurl, revision);
        return new SVNReference(
                path,
                url,
                info.getRevision().getNumber(),
                new DateTime(info.getCommittedDate())
        );
    }

    @Override
    public SVNReference getReference(SVNLocation location) {
        return getReference(location.getPath(), SVNRevision.create(location.getRevision()));
    }

    private SVNInfo getInfo(SVNURL url, SVNRevision revision) {
        try {
            return getWCClient().doInfo(url, revision, revision);
        } catch (SVNException e) {
            throw translateSVNException(e);
        }
    }

    @Override
    public boolean isTagOrBranch(String path) {
        return isTag(path) || isBranch(path);
    }

    @Override
    public boolean isTrunkOrBranch(String path) {
        return isTrunk(path) || isBranch(path);
    }

    @Override
    public boolean isTag(String path) {
        return isPathOK(configurationExtension.getTagPattern(), path);
    }

    private boolean isBranch(String path) {
        return isPathOK(configurationExtension.getBranchPattern(), path);
    }

    private boolean isPathOK(String pattern, String path) {
        return org.apache.commons.lang.StringUtils.isNotBlank(pattern) && Pattern.matches(pattern, path);
    }

    private boolean isTrunk(String path) {
        return org.apache.commons.lang.StringUtils.isNotBlank(path) && Pattern.matches(".+/trunk", path);
    }

    private RuntimeException translateSVNException(SVNException e) {
        // FIXME Uses a proper exception
        return new RuntimeException(e);
    }

    protected SVNWCClient getWCClient() {
        return getClientManager().getWCClient();
    }

    protected SVNLogClient getLogClient() {
        return getClientManager().getLogClient();
    }

    protected SVNDiffClient getDiffClient() {
        return getClientManager().getDiffClient();
    }

    protected SVNClientManager getClientManager() {
        // Gets the current transaction
        Transaction transaction = transactionService.get();
        if (transaction == null) {
            throw new IllegalStateException("All SVN calls must be part of a SVN transaction");
        }
        // Gets the client manager
        return transaction.getResource(SVNSession.class).getClientManager();
    }
}
