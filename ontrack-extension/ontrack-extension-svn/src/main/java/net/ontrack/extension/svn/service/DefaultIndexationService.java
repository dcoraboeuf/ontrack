package net.ontrack.extension.svn.service;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.ontrack.core.model.UserMessage;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.extension.api.ExtensionManager;
import net.ontrack.extension.jira.JIRAConfigurationService;
import net.ontrack.extension.jira.JIRAService;
import net.ontrack.extension.jira.service.model.JIRAConfiguration;
import net.ontrack.extension.svn.IndexationConfigurationExtension;
import net.ontrack.extension.svn.SubversionConfigurationExtension;
import net.ontrack.extension.svn.SubversionExtension;
import net.ontrack.extension.svn.dao.IssueRevisionDao;
import net.ontrack.extension.svn.dao.RevisionDao;
import net.ontrack.extension.svn.dao.SVNEventDao;
import net.ontrack.extension.svn.dao.model.TRevision;
import net.ontrack.extension.svn.service.model.LastRevisionInfo;
import net.ontrack.extension.svn.support.SVNUtils;
import net.ontrack.service.InfoProvider;
import net.ontrack.service.api.ScheduledService;
import net.ontrack.tx.Transaction;
import net.ontrack.tx.TransactionService;
import net.sf.jstring.Localizable;
import net.sf.jstring.LocalizableMessage;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

// TODO There should not be any dependency on the JIRA module!
@Service
public class DefaultIndexationService implements IndexationService, ScheduledService, InfoProvider {

    private final Logger logger = LoggerFactory.getLogger(IndexationService.class);
    private final IndexationConfigurationExtension indexationConfigurationExtension;
    private final SubversionConfigurationExtension subversionConfigurationExtension;
    private final TransactionService transactionService;
    private final SubversionService subversionService;
    private final JIRAService jiraService;
    private final JIRAConfigurationService jiraConfigurationService;
    private final RevisionDao revisionDao;
    private final SVNEventDao svnEventDao;
    private final IssueRevisionDao issueRevisionDao;
    private final TransactionTemplate transactionTemplate;
    private final ExtensionManager extensionManager;
    // Current indexation
    private final AtomicReference<IndexationJob> currentIndexationJob = new AtomicReference<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setDaemon(true).setNameFormat("Indexation %s").build());

    @Autowired
    public DefaultIndexationService(PlatformTransactionManager transactionManager, IndexationConfigurationExtension indexationConfigurationExtension, SubversionConfigurationExtension subversionConfigurationExtension, TransactionService transactionService, SubversionService subversionService, JIRAService jiraService, JIRAConfigurationService jiraConfigurationService, RevisionDao revisionDao, SVNEventDao svnEventDao, IssueRevisionDao issueRevisionDao, ExtensionManager extensionManager) {
        this.indexationConfigurationExtension = indexationConfigurationExtension;
        this.subversionConfigurationExtension = subversionConfigurationExtension;
        this.transactionService = transactionService;
        this.subversionService = subversionService;
        this.jiraService = jiraService;
        this.jiraConfigurationService = jiraConfigurationService;
        this.revisionDao = revisionDao;
        this.svnEventDao = svnEventDao;
        this.issueRevisionDao = issueRevisionDao;
        this.extensionManager = extensionManager;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    protected void indexTask() {
        logger.info("[indexation] Indexation task starting...");
        // Checks if there is running indexation for this repository
        if (isIndexationRunning()) {
            // Log
            logger.info("[indexation] An indexation is already running. Will try later");
        } else {
            // Launches the indexation
            indexFromLatest();
        }
        logger.info("[indexation] Indexation task stopped.");
    }

    @Override
    public boolean isIndexationRunning() {
        IndexationJob job = currentIndexationJob.get();
        return job != null && job.isRunning();
    }

    @Override
    public UserMessage getInfo() {
        // Gets the current job
        IndexationJob job = currentIndexationJob.get();
        if (job != null) {
            Localizable message = new LocalizableMessage(
                    "subversion.indexation.message",
                    job.isRunning() ? new LocalizableMessage("subversion.indexation.running") : new LocalizableMessage("subversion.indexation.pending"),
                    job.getMin(), job.getMax(),
                    job.getCurrent(),
                    job.getProgress());
            return UserMessage.info(message);
        } else {
            return null;
        }
    }

    @Override
    @Secured(SecurityRoles.ADMINISTRATOR)
    public void indexFromLatest() {
        try (Transaction ignored = transactionService.start()) {
            // Loads the repository information
            SVNURL url = SVNUtils.toURL(subversionConfigurationExtension.getUrl());
            // Last scanned revision
            long lastScannedRevision = revisionDao.getLast();
            if (lastScannedRevision <= 0) {
                lastScannedRevision = indexationConfigurationExtension.getStartRevision();
            }
            // Logging
            logger.info("Submitting indexation from latest scanned revision: " + lastScannedRevision);
            // HEAD revision
            long repositoryRevision = subversionService.getRepositoryRevision(url);
            // Request index of the range
            indexRange(lastScannedRevision + 1, repositoryRevision);
        }
    }

    @Override
    public LastRevisionInfo getLastRevisionInfo(int repositoryId) {
        try (Transaction ignored = transactionService.start()) {
            TRevision r = revisionDao.getLastRevision(repositoryId);
            if (r != null) {
                // Loads the repository information
                SVNURL url = SVNUtils.toURL(subversionService.getRepository(repositoryId).getUrl());
                long repositoryRevision = subversionService.getRepositoryRevision(url);
                // OK
                return new LastRevisionInfo(
                        r.getRevision(),
                        r.getMessage(),
                        repositoryRevision
                );
            } else {
                return new LastRevisionInfo(
                        0L,
                        "",
                        0L
                );
            }
        }
    }

    @Override
    @Secured(SecurityRoles.ADMINISTRATOR)
    public void indexRange(Long from, Long to) {
        logger.info("[indexation] Submitting indexation of range from " + from + " to " + to);
        long min;
        long max;
        if (from == null) {
            min = max = to;
        } else if (to == null) {
            min = max = from;
        } else {
            min = Math.min(from, to);
            max = Math.max(from, to);
        }
        // Indexation job
        DefaultIndexationJob job = new DefaultIndexationJob(min, max);
        currentIndexationJob.set(job);
        // Schedule the scan
        executor.submit(job);
    }

    @Override
    @Secured(SecurityRoles.ADMINISTRATOR)
    public void reindex() {
        // Clear all existing data
        revisionDao.deleteAll();
        // OK, launches a new indexation
        indexFromLatest();
    }

    /**
     * Indexation of a range in a thread - since it is called by a single thread executor, we can be sure that only one
     * call of this method is running at one time.
     */
    protected void index(long from, long to, IndexationListener indexationListener) {
        // Ordering
        if (from > to) {
            long t = from;
            from = to;
            to = t;
        }

        // Opens a transaction
        try (Transaction ignored = transactionService.start()) {
            // Gets the 'default' JIRA configuration
            // TODO #306 The JIRA configuration must be extracted from the SVN configuration
            JIRAConfiguration jiraConfiguration = jiraConfigurationService.getConfigurationByName("default");
            // SVN URL
            SVNURL url = SVNUtils.toURL(subversionConfigurationExtension.getUrl());
            // Filters the revision range using the repository configuration
            long startRevision = indexationConfigurationExtension.getStartRevision();
            from = Math.max(startRevision, from);
            // Filters the revision range using the SVN repository
            long repositoryRevision = subversionService.getRepositoryRevision(url);
            to = Math.min(to, repositoryRevision);
            // Final check of range
            if (from > to) {
                throw new IllegalArgumentException(String.format("Cannot index range from %d to %d", from, to));
            }
            // Log
            logger.info(String.format("[indexation] Indexing revisions from %d to %d", from, to));
            // SVN range
            SVNRevision fromRevision = SVNRevision.create(from);
            SVNRevision toRevision = SVNRevision.create(to);
            // Calls the indexer, including merge revisions
            IndexationHandler handler = new IndexationHandler(jiraConfiguration, indexationListener);
            subversionService.log(url, SVNRevision.HEAD, fromRevision, toRevision, true, true, 0, false, handler);
        }
    }

    /**
     * This method is executed within a transaction
     */
    private void indexInTransaction(JIRAConfiguration jiraConfiguration, SVNLogEntry logEntry) throws SVNException {
        // Log values
        long revision = logEntry.getRevision();
        String author = logEntry.getAuthor();
        // Date to date time
        Date date = logEntry.getDate();
        DateTime dateTime = new DateTime(date.getTime(), DateTimeZone.UTC);
        // Message
        String message = logEntry.getMessage();
        // Branch for the revision
        String branch = getBranchForRevision(logEntry);
        // Logging
        logger.info(String.format("Indexing revision %d", revision));
        // Inserting or updating the revision
        revisionDao.addRevision(revision, author, dateTime, message, branch);
        // Merge relationships (using a nested SVN client)
        try (Transaction ignored = transactionService.start(true)) {
            List<Long> mergedRevisions = subversionService.getMergedRevisions(SVNUtils.toURL(subversionConfigurationExtension.getUrl(), branch), revision);
            revisionDao.addMergedRevisions(revision, mergedRevisions);
        }
        // Subversion events
        indexSVNEvents(logEntry);
        // Indexes the issues
        indexIssues(jiraConfiguration, logEntry);
    }

    private void indexIssues(JIRAConfiguration jiraConfiguration, SVNLogEntry logEntry) {
        long revision = logEntry.getRevision();
        String message = logEntry.getMessage();
        // Cache for issues
        Set<String> revisionIssues = new HashSet<>();
        // Gets all issues
        // FIXME Indexation is dependent on the project!
        Set<String> issues = jiraService.extractIssueKeysFromMessage(0, message);
        // For each issue in the message
        for (String issueKey : issues) {
            // Checks that the issue has not already been associated with this revision
            if (!revisionIssues.contains(issueKey)) {
                revisionIssues.add(issueKey);
                // Indexes this issue
                issueRevisionDao.link(revision, issueKey);
            }
        }
    }

    private void indexSVNEvents(SVNLogEntry logEntry) {
        indexSVNCopyEvents(logEntry);
        indexSVNStopEvents(logEntry);
    }

    private void indexSVNCopyEvents(SVNLogEntry logEntry) {
        long revision = logEntry.getRevision();
        // Looking for copy tags
        @SuppressWarnings("unchecked")
        Map<String, SVNLogEntryPath> changedPaths = logEntry.getChangedPaths();
        // Copies
        /*
         * Looks through all changed paths and retains only copy operations toward branches or tags
		 */
        for (SVNLogEntryPath logEntryPath : changedPaths.values()) {
            // Gets the copy path
            String copyFromPath = logEntryPath.getCopyPath();
            if (StringUtils.isNotBlank(copyFromPath) && logEntryPath.getType() == SVNLogEntryPath.TYPE_ADDED) {
                // Registers the new history
                String copyToPath = logEntryPath.getPath();
                // Retains only branches and tags
                if (subversionService.isTagOrBranch(copyToPath)) {
                    long copyFromRevision = logEntryPath.getCopyRevision();
                    logger.debug(String.format("\tCOPY %s@%d --> %s", copyFromPath, copyFromRevision, copyToPath));
                    // Adds a copy event
                    svnEventDao.createCopyEvent(revision, copyFromPath, copyFromRevision, copyToPath);
                }
            }
        }
    }

    private void indexSVNStopEvents(SVNLogEntry logEntry) {
        long revision = logEntry.getRevision();
        // Looking for copy tags
        @SuppressWarnings("unchecked")
        Map<String, SVNLogEntryPath> changedPaths = logEntry.getChangedPaths();
        // For all changes path
        for (SVNLogEntryPath logEntryPath : changedPaths.values()) {
            String path = logEntryPath.getPath();
            if (logEntryPath.getType() == SVNLogEntryPath.TYPE_DELETED && subversionService.isTagOrBranch(path)) {
                logger.debug(String.format("\tSTOP %s", path));
                // Adds the stop event
                svnEventDao.createStopEvent(revision, path);
            }
        }
    }

    private String getBranchForRevision(SVNLogEntry logEntry) {
        // List of paths for this revision
        @SuppressWarnings("unchecked")
        Set<String> paths = logEntry.getChangedPaths().keySet();
        // Finds the common path among all those paths
        String commonPath = null;
        for (String path : paths) {
            if (commonPath == null) {
                commonPath = path;
            } else {
                int diff = StringUtils.indexOfDifference(commonPath, path);
                commonPath = StringUtils.left(commonPath, diff);
            }
        }
        // Gets the branch for this path
        if (commonPath != null) {
            return extractBranch(commonPath);
        } else {
            // No path in the revision: no branch!
            return null;
        }
    }

    protected String extractBranch(String path) {
        if (subversionService.isTrunkOrBranch(path)) {
            return path;
        } else {
            String before = StringUtils.substringBeforeLast(path, "/");
            if (StringUtils.isBlank(before)) {
                return null;
            } else {
                return extractBranch(before);
            }
        }
    }

    @Override
    public Runnable getTask() {
        return new Runnable() {
            @Override
            public void run() {
                // Configuration
                int scanInterval = indexationConfigurationExtension.getScanInterval();
                if (scanInterval > 0 && extensionManager.isExtensionEnabled(SubversionExtension.EXTENSION)) {
                    indexTask();
                }
            }
        };
    }

    @Override
    public Trigger getTrigger() {
        return new Trigger() {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                // Configuration
                int scanInterval = indexationConfigurationExtension.getScanInterval();
                // No scan, tries again in one minute, in case the configuration has changed
                if (scanInterval <= 0) {
                    return DateTime.now().plusMinutes(1).toDate();
                } else {
                    // Last execution time
                    Date time = triggerContext.lastActualExecutionTime();
                    if (time != null) {
                        DateTime dateTime = new DateTime(time.getTime());
                        return dateTime.plusMinutes(scanInterval).toDate();
                    } else {
                        // Never executed before
                        return DateTime.now().plusMinutes(scanInterval).toDate();
                    }
                }
            }
        };
    }

    private static interface IndexationListener {

        void setRevision(long revision);

    }

    private class IndexationHandler implements ISVNLogEntryHandler {

        private final JIRAConfiguration jiraConfiguration;
        private final IndexationListener indexationListener;

        public IndexationHandler(JIRAConfiguration jiraConfiguration, IndexationListener indexationListener) {
            this.jiraConfiguration = jiraConfiguration;
            this.indexationListener = indexationListener;
        }

        @Override
        public void handleLogEntry(final SVNLogEntry logEntry) throws SVNException {
            // Transaction
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {

                @Override
                protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                    try {
                        indexationListener.setRevision(logEntry.getRevision());
                        indexInTransaction(jiraConfiguration, logEntry);
                    } catch (Exception ex) {
                        logger.error("Cannot index revision " + logEntry.getRevision(), ex);
                        throw new RuntimeException(ex);
                    }
                }
            });
        }
    }

    private class DefaultIndexationJob implements IndexationJob, Runnable, IndexationListener {

        private final long min;
        private final long max;
        private boolean running;
        private long current;

        private DefaultIndexationJob(long min, long max) {
            this.min = min;
            this.max = max;
            this.current = min;
        }

        @Override
        public boolean isRunning() {
            return running;
        }

        @Override
        public long getMin() {
            return min;
        }

        @Override
        public long getMax() {
            return max;
        }

        @Override
        public long getCurrent() {
            return current;
        }

        @Override
        public int getProgress() {
            double value = (current - min) / (double) (max - min);
            return (int) (value * 100);
        }

        @Override
        public void run() {
            try {
                running = true;
                index(min, max, this);
            } catch (Exception ex) {
                logger.error(String.format("Could not index range from %s to %s", min, max), ex);
            } finally {
                currentIndexationJob.set(null);
            }
        }

        @Override
        public void setRevision(long revision) {
            this.current = revision;
        }
    }
}
