package net.ontrack.extension.svn.service;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import net.ontrack.core.model.Entity;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.extension.api.property.PropertiesService;
import net.ontrack.extension.svn.SVNEventType;
import net.ontrack.extension.svn.SubversionExtension;
import net.ontrack.extension.svn.SubversionRepositoryPropertyExtension;
import net.ontrack.extension.svn.dao.IssueRevisionDao;
import net.ontrack.extension.svn.dao.RevisionDao;
import net.ontrack.extension.svn.dao.SVNEventDao;
import net.ontrack.extension.svn.dao.model.TRevision;
import net.ontrack.extension.svn.dao.model.TSVNCopyEvent;
import net.ontrack.extension.svn.dao.model.TSVNEvent;
import net.ontrack.extension.svn.service.model.*;
import net.ontrack.extension.svn.support.SVNLogEntryCollector;
import net.ontrack.extension.svn.support.SVNUtils;
import net.ontrack.extension.svn.tx.DefaultSVNSession;
import net.ontrack.extension.svn.tx.SVNSession;
import net.ontrack.tx.Transaction;
import net.ontrack.tx.TransactionResourceProvider;
import net.ontrack.tx.TransactionService;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.wc.*;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DefaultSubversionService implements SubversionService {

    public static final int HISTORY_MAX_DEPTH = 6;
    private final DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    private final Pattern pathWithRevision = Pattern.compile("(.*)@(\\d+)$");
    private final RepositoryService repositoryService;
    private final TransactionService transactionService;
    private final PropertiesService propertiesService;
    private final SVNEventDao svnEventDao;
    private final RevisionDao revisionDao;
    private final IssueRevisionDao issueRevisionDao;
    private final SecurityUtils securityUtils;

    @Autowired
    public DefaultSubversionService(RepositoryService repositoryService, TransactionService transactionService, PropertiesService propertiesService, SVNEventDao svnEventDao, RevisionDao revisionDao, IssueRevisionDao issueRevisionDao, SecurityUtils securityUtils) {
        this.repositoryService = repositoryService;
        this.transactionService = transactionService;
        this.propertiesService = propertiesService;
        this.svnEventDao = svnEventDao;
        this.revisionDao = revisionDao;
        this.issueRevisionDao = issueRevisionDao;
        this.securityUtils = securityUtils;
        SVNRepositoryFactoryImpl.setup();
        DAVRepositoryFactory.setup();
    }

    @Override
    public String formatRevisionTime(DateTime time) {
        return format.print(time);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isClosed(SVNRepository repository, String path) {
        TSVNEvent lastEvent = svnEventDao.getLastEvent(repository.getId(), path);
        return lastEvent != null && lastEvent.getType() == SVNEventType.STOP;
    }

    @Override
    @Transactional(readOnly = true)
    public SVNLocation getFirstCopyAfter(SVNRepository repository, SVNLocation location) {
        return svnEventDao.getFirstCopyAfter(repository.getId(), location);
    }

    @Override
    @Transactional(readOnly = true)
    public SVNRepository getRepositoryForProject(int projectId) {
        // Gets the SVN Repository property on the project
        String repositoryIdValue = propertiesService.getPropertyValue(Entity.PROJECT, projectId, SubversionExtension.EXTENSION, SubversionRepositoryPropertyExtension.NAME);
        if (StringUtils.isNotBlank(repositoryIdValue)) {
            int repositoryId = Integer.parseInt(repositoryIdValue, 10);
            return repositoryService.getRepository(repositoryId);
        } else {
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public SVNRevisionPaths getRevisionPaths(SVNRepository repository, long revision) {
        // Result
        final SVNRevisionPaths paths = new SVNRevisionPaths(getRevisionInfo(repository, revision));
        // Gets the URL of the repository
        SVNURL rootUrl = repository.getSVNURL();
        // Gets the diff for the revision
        try {
            getDiffClient(repository).doDiffStatus(
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
    public String getFileChangeBrowsingURL(SVNRepository repository, String path, long revision) {
        String browserForPathAndRevision = repository.getBrowserForChange();
        if (StringUtils.isNotBlank(browserForPathAndRevision)) {
            return browserForPathAndRevision.replace("*", path).replace("$", String.valueOf(revision));
        } else {
            return getURL(repository, path);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<SVNLocation> getCopiesFrom(SVNRepository repository, SVNLocation location, SVNLocationSortMode sortMode) {
        return svnEventDao.getCopiesFrom(repository.getId(), location, sortMode);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isIndexedIssue(final String key) {
        return Iterables.any(
                repositoryService.getAllRepositories(),
                new Predicate<SVNRepository>() {
                    @Override
                    public boolean apply(SVNRepository repository) {
                        return issueRevisionDao.isIndexed(repository.getId(), key);
                    }
                }
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getRevisionsForIssueKey(SVNRepository repository, String key) {
        return issueRevisionDao.findRevisionsByIssue(repository.getId(), key);
    }

    @Override
    public String getURL(SVNRepository repository, String path) {
        return repository.getUrl() + path;
    }

    @Override
    public String getBrowsingURL(SVNRepository repository, String path) {
        String browserForPath = repository.getBrowserForPath();
        if (StringUtils.isNotBlank(browserForPath)) {
            return browserForPath.replace("*", path);
        } else {
            return getURL(repository, path);
        }
    }

    @Override
    public String getRevisionBrowsingURL(SVNRepository repository, long revision) {
        String browserForPath = repository.getBrowserForRevision();
        if (StringUtils.isNotBlank(browserForPath)) {
            return browserForPath.replace("*", String.valueOf(revision));
        } else {
            return String.valueOf(revision);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public SVNRevisionInfo getRevisionInfo(SVNRepository repository, long revision) {
        TRevision t = revisionDao.get(repository.getId(), revision);
        return new SVNRevisionInfo(
                t.getRevision(),
                t.getAuthor(),
                t.getCreation(),
                t.getBranch(),
                formatRevisionTime(t.getCreation()),
                t.getMessage(),
                getRevisionBrowsingURL(repository, t.getRevision())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getIssueKeysForRevision(SVNRepository repository, long revision) {
        return issueRevisionDao.findIssuesByRevision(repository.getId(), revision);
    }

    @Override
    public long getRepositoryRevision(SVNRepository repository, SVNURL url) {
        try {
            SVNInfo info = getWCClient(repository).doInfo(url, SVNRevision.HEAD, SVNRevision.HEAD);
            return info.getCommittedRevision().getNumber();
        } catch (SVNException e) {
            throw translateSVNException(e);
        }
    }

    @Override
    public void log(SVNRepository repository, SVNURL url, SVNRevision pegRevision, SVNRevision startRevision, SVNRevision stopRevision, boolean stopOnCopy, boolean discoverChangedPaths, long limit, boolean includeMergedRevisions, ISVNLogEntryHandler isvnLogEntryHandler) {
        try {
            getLogClient(repository).doLog(url, null, pegRevision, startRevision, stopRevision, stopOnCopy, discoverChangedPaths,
                    includeMergedRevisions, limit, null, isvnLogEntryHandler);
        } catch (SVNException e) {
            throw translateSVNException(e);
        }
    }

    @Override
    public boolean exists(SVNRepository repository, SVNURL url, SVNRevision revision) {
        // Tries to gets information
        try {
            SVNInfo info = getWCClient(repository).doInfo(url, revision, revision);
            return info != null;
        } catch (SVNException ex) {
            return false;
        }
    }

    @Override
    public List<Long> getMergedRevisions(SVNRepository repository, SVNURL url, long revision) {
        // Checks that the URL exists at both R-1 and R
        SVNRevision rm1 = SVNRevision.create(revision - 1);
        SVNRevision r = SVNRevision.create(revision);
        boolean existRM1 = exists(repository, url, rm1);
        boolean existR = exists(repository, url, r);
        try {
            // Both revisions must be valid in order to get some merges in between
            if (existRM1 && existR) {
                // Gets the changes in merge information
                SVNDiffClient diffClient = getDiffClient(repository);
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
                            log(repository, source, endRevision, startRevision, endRevision, true, false, 0, false, collector);
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
    @Transactional(readOnly = true)
    public List<Long> getMergesForRevision(SVNRepository repository, long revision) {
        return revisionDao.getMergesForRevision(repository.getId(), revision);
    }

    @Override
    public SVNHistory getHistory(int repositoryId, String path) {
        // Configuration
        SVNRepository repository = repositoryService.getRepository(repositoryId);
        // Gets the reference for this first path
        SVNReference reference = getReference(repository, path);
        // Initializes the history
        SVNHistory history = new SVNHistory();
        // Adds the initial reference if this a branch or trunk
        if (isTrunkOrBranch(repository, reference.getPath())) {
            history = history.add(reference);
        }
        // Loops on copies
        int depth = HISTORY_MAX_DEPTH;
        while (reference != null && depth > 0) {
            depth--;
            // Gets the reference of the source
            SVNReference origin = getOrigin(repository, reference);
            if (origin != null) {
                // Adds to the history if this a branch or trunk
                if (isTrunkOrBranch(repository, origin.getPath())) {
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

    private SVNReference getOrigin(SVNRepository repository, SVNReference destination) {
        // Gets the last copy event
        TSVNCopyEvent copyEvent = svnEventDao.getLastCopyEvent(repository.getId(), destination.getPath(), destination.getRevision());
        if (copyEvent != null) {
            return getReference(repository, copyEvent.getCopyFromPath(), SVNRevision.create(copyEvent.getCopyFromRevision()));
        } else {
            return null;
        }
    }

    private SVNReference getReference(SVNRepository repository, String path) {
        Matcher matcher = pathWithRevision.matcher(path);
        if (matcher.matches()) {
            String pathOnly = matcher.group(1);
            long revision = Long.parseLong(matcher.group(2), 10);
            return getReference(repository, pathOnly, SVNRevision.create(revision));
        } else {
            return getReference(repository, path, SVNRevision.HEAD);
        }
    }

    private SVNReference getReference(SVNRepository repository, String path, SVNRevision revision) {
        String url = getURL(repository, path);
        SVNURL svnurl = SVNUtils.toURL(url);
        SVNInfo info = getInfo(repository, svnurl, revision);
        return new SVNReference(
                path,
                url,
                info.getRevision().getNumber(),
                new DateTime(info.getCommittedDate())
        );
    }

    @Override
    public SVNReference getReference(SVNRepository repository, SVNLocation location) {
        return getReference(repository, location.getPath(), SVNRevision.create(location.getRevision()));
    }

    private SVNInfo getInfo(SVNRepository repository, SVNURL url, SVNRevision revision) {
        try {
            return getWCClient(repository).doInfo(url, revision, revision);
        } catch (SVNException e) {
            throw translateSVNException(e);
        }
    }

    @Override
    public boolean isTagOrBranch(SVNRepository repository, String path) {
        return isTag(repository, path) || isBranch(repository, path);
    }

    @Override
    public boolean isTrunkOrBranch(SVNRepository repository, String path) {
        return isTrunk(path) || isBranch(repository, path);
    }

    @Override
    public boolean isTag(SVNRepository repository, String path) {
        return isPathOK(repository.getTagPattern(), path);
    }

    private boolean isBranch(SVNRepository repository, String path) {
        return isPathOK(repository.getBranchPattern(), path);
    }

    private boolean isPathOK(String pattern, String path) {
        return org.apache.commons.lang.StringUtils.isNotBlank(pattern) && Pattern.matches(pattern, path);
    }

    private boolean isTrunk(String path) {
        return isPathOK(".+/trunk", path);
    }

    private SubversionException translateSVNException(SVNException e) {
        return new SubversionException(e);
    }

    protected SVNWCClient getWCClient(SVNRepository repository) {
        return getClientManager(repository).getWCClient();
    }

    protected SVNLogClient getLogClient(SVNRepository repository) {
        return getClientManager(repository).getLogClient();
    }

    protected SVNDiffClient getDiffClient(SVNRepository repository) {
        return getClientManager(repository).getDiffClient();
    }

    protected SVNClientManager getClientManager(final SVNRepository repository) {
        // Gets the current transaction
        Transaction transaction = transactionService.get();
        if (transaction == null) {
            throw new IllegalStateException("All SVN calls must be part of a SVN transaction");
        }
        // Gets the client manager
        return transaction
                .getResource(
                        SVNSession.class,
                        repository.getId(),
                        new TransactionResourceProvider<SVNSession>() {
                            @Override
                            public SVNSession createTxResource() {
                                // Creates the client manager for SVN
                                SVNClientManager clientManager = SVNClientManager.newInstance();
                                // Authentication (if needed)
                                String svnUser = repository.getUser();
                                String svnPassword = securityUtils.asAdmin(
                                        new Callable<String>() {
                                            @Override
                                            public String call() throws Exception {
                                                return repositoryService.getPassword(repository.getId());
                                            }
                                        }
                                );
                                if (StringUtils.isNotBlank(svnUser) && StringUtils.isNotBlank(svnPassword)) {
                                    clientManager.setAuthenticationManager(new BasicAuthenticationManager(svnUser, svnPassword));
                                }
                                // OK
                                return new DefaultSVNSession(clientManager);
                            }
                        }
                )
                .getClientManager();
    }
}
