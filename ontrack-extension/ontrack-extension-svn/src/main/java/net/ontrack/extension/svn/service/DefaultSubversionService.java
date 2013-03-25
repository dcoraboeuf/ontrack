package net.ontrack.extension.svn.service;

import net.ontrack.extension.svn.SubversionConfigurationExtension;
import net.ontrack.extension.svn.SubversionService;
import net.ontrack.extension.svn.support.SVNLogEntryCollector;
import net.ontrack.extension.svn.tx.SVNSession;
import net.ontrack.tx.Transaction;
import net.ontrack.tx.TransactionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.wc.*;

import java.util.*;
import java.util.regex.Pattern;

@Service
public class DefaultSubversionService implements SubversionService {

    private final SubversionConfigurationExtension configurationExtension;
    private final TransactionService transactionService;

    @Autowired
    public DefaultSubversionService(SubversionConfigurationExtension configurationExtension, TransactionService transactionService) {
        this.configurationExtension = configurationExtension;
        this.transactionService = transactionService;
    }

    @Override
    public String getURL(String path) {
        return configurationExtension.getConfiguration().getUrl() + path;
    }

    @Override
    public String getBrowsingURL(String path) {
        String browserForPath = configurationExtension.getConfiguration().getBrowserForPath();
        if (StringUtils.isNotBlank(browserForPath)) {
            return browserForPath.replace("*", path);
        } else {
            return getURL(path);
        }
    }

    @Override
    public long getRepositoryRevision(SVNURL url) {
        try {
            SVNInfo info = getWCClient().doInfo(url, SVNRevision.HEAD, SVNRevision.HEAD);
            return info.getRevision().getNumber();
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
    public boolean isTagOrBranch(String path) {
        return isTag(path) || isBranch(path);
    }

    private boolean isTag(String path) {
        return isPathOK(configurationExtension.getConfiguration().getTagPattern(), path);
    }

    @Override
    public boolean isTrunkOrBranch(String path) {
        return isTrunk(path) || isBranch(path);
    }

    private boolean isBranch(String path) {
        return isPathOK(configurationExtension.getConfiguration().getBranchPattern(), path);
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
