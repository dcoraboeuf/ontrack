package net.ontrack.extension.svn.service;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.ontrack.core.model.UserMessage;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.extension.api.ExtensionManager;
import net.ontrack.extension.svn.SubversionExtension;
import net.ontrack.extension.svn.dao.RevisionDao;
import net.ontrack.extension.svn.dao.SVNEventDao;
import net.ontrack.extension.svn.dao.model.TRevision;
import net.ontrack.extension.svn.service.model.LastRevisionInfo;
import net.ontrack.extension.svn.service.model.SVNRepository;
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
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class DefaultIndexationService implements IndexationService, ScheduledService, InfoProvider {

    private final Logger logger = LoggerFactory.getLogger(IndexationService.class);
    private final TransactionService transactionService;
    private final SubversionService subversionService;
    private final RevisionDao revisionDao;
    private final SVNEventDao svnEventDao;
    private final TransactionTemplate transactionTemplate;
    private final ExtensionManager extensionManager;
    private final SecurityUtils securityUtils;
    // Current indexations
    private final Map<Integer, IndexationJob> indexationJobs = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(5, new ThreadFactoryBuilder().setDaemon(true).setNameFormat("Indexation %s").build());

    @Autowired
    public DefaultIndexationService(PlatformTransactionManager transactionManager, TransactionService transactionService, SubversionService subversionService, RevisionDao revisionDao, SVNEventDao svnEventDao, ExtensionManager extensionManager, SecurityUtils securityUtils) {
        this.transactionService = transactionService;
        this.subversionService = subversionService;
        this.revisionDao = revisionDao;
        this.svnEventDao = svnEventDao;
        this.extensionManager = extensionManager;
        this.securityUtils = securityUtils;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    protected void indexTask(SVNRepository repository) {
        final int repositoryId = repository.getId();
        logger.info("[svn-indexation] Repository={}, Indexation task starting...", repositoryId);
        // Checks if there is running indexation for this repository
        if (isIndexationRunning(repositoryId)) {
            // Log
            logger.info("[indexation] Repository={}, An indexation is already running. Will try later", repositoryId);
        } else {
            // Launches the indexation, using admin rights
            securityUtils.asAdmin(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    indexFromLatest(repositoryId);
                    return null;
                }
            });
        }
        logger.info("[indexation] Repository={}, Indexation task stopped.", repositoryId);
    }

    @Override
    public boolean isIndexationRunning(int repositoryId) {
        IndexationJob job = indexationJobs.get(repositoryId);
        return job != null && job.isRunning();
    }

    @Override
    public Collection<UserMessage> getInfo() {
        Collection<UserMessage> messages = new ArrayList<>();
        List<SVNRepository> repositories = securityUtils.asAdmin(new Callable<List<SVNRepository>>() {
            @Override
            public List<SVNRepository> call() throws Exception {
                return subversionService.getAllRepositories();
            }
        });
        for (SVNRepository repository : repositories) {
            IndexationJob job = indexationJobs.get(repository.getId());
            if (job != null) {
                Localizable message = new LocalizableMessage(
                        "subversion.indexation.message",
                        job.isRunning() ? new LocalizableMessage("subversion.indexation.running") : new LocalizableMessage("subversion.indexation.pending"),
                        job.getMin(), job.getMax(),
                        job.getCurrent(),
                        job.getProgress(),
                        repository.getName());
                messages.add(UserMessage.info(message));
            }
        }
        return messages;
    }

    @Override
    @Secured(SecurityRoles.ADMINISTRATOR)
    public void indexFromLatest(int repositoryId) {
        try (Transaction ignored = transactionService.start()) {
            // Loads the repository information
            SVNRepository repository = subversionService.getRepository(repositoryId);
            SVNURL url = SVNUtils.toURL(repository.getUrl());
            // Last scanned revision
            long lastScannedRevision = revisionDao.getLast(repositoryId);
            if (lastScannedRevision <= 0) {
                lastScannedRevision = repository.getIndexationStart();
            }
            // Logging
            logger.info("[svn-indexation] Repository={}, LastScannedRevision={}", repositoryId, lastScannedRevision);
            // HEAD revision
            long repositoryRevision = subversionService.getRepositoryRevision(repository, url);
            // Request index of the range
            indexRange(repositoryId, lastScannedRevision + 1, repositoryRevision);
        }
    }

    @Override
    public LastRevisionInfo getLastRevisionInfo(int repositoryId) {
        try (Transaction ignored = transactionService.start()) {
            TRevision r = revisionDao.getLastRevision(repositoryId);
            if (r != null) {
                // Loads the repository information
                SVNRepository repository = subversionService.getRepository(repositoryId);
                SVNURL url = SVNUtils.toURL(repository.getUrl());
                long repositoryRevision = subversionService.getRepositoryRevision(repository, url);
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
    public void indexRange(int repositoryId, Long from, Long to) {
        logger.info("[svn-indexation] Repository={}, Range={}->{}", repositoryId, from, to);
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
        DefaultIndexationJob job = new DefaultIndexationJob(subversionService.getRepository(repositoryId), min, max);
        indexationJobs.put(repositoryId, job);
        // Schedule the scan
        executor.submit(job);
    }

    @Override
    @Secured(SecurityRoles.ADMINISTRATOR)
    public void reindex(int repositoryId) {
        // Clear all existing data
        revisionDao.deleteAll(repositoryId);
        // OK, launches a new indexation
        indexFromLatest(repositoryId);
    }

    /**
     * Indexation of a range in a thread for one repository - since it is called by a single thread executor, we can
     * be sure that only one call of this method is running at one time for one given repository.
     */
    protected void index(SVNRepository repository, long from, long to, IndexationListener indexationListener) {
        // Ordering
        if (from > to) {
            long t = from;
            from = to;
            to = t;
        }

        // Opens a transaction
        try (Transaction ignored = transactionService.start()) {
            // FIXME The repository must be linked with an `IssueMessageScanner` at configuration level
            // SVN URL
            SVNURL url = SVNUtils.toURL(repository.getUrl());
            // Filters the revision range using the repository configuration
            long startRevision = repository.getIndexationStart();
            from = Math.max(startRevision, from);
            // Filters the revision range using the SVN repository
            long repositoryRevision = subversionService.getRepositoryRevision(repository, url);
            to = Math.min(to, repositoryRevision);
            // Final check of range
            if (from > to) {
                throw new IllegalArgumentException(String.format("Cannot index range from %d to %d", from, to));
            }
            // Log
            logger.info("[svn-indexation] Repository={}, Range: {}-{}", repository.getId(), from, to);
            // SVN range
            SVNRevision fromRevision = SVNRevision.create(from);
            SVNRevision toRevision = SVNRevision.create(to);
            // Calls the indexer, including merge revisions
            IndexationHandler handler = new IndexationHandler(repository, /*jiraConfiguration, */indexationListener);
            subversionService.log(repository, url, SVNRevision.HEAD, fromRevision, toRevision, true, true, 0, false, handler);
        }
    }

    /**
     * This method is executed within a transaction
     */
    private void indexInTransaction(SVNRepository repository, SVNLogEntry logEntry) throws SVNException {
        // Log values
        long revision = logEntry.getRevision();
        String author = logEntry.getAuthor();
        // Date to date time
        Date date = logEntry.getDate();
        DateTime dateTime = new DateTime(date.getTime(), DateTimeZone.UTC);
        // Message
        String message = logEntry.getMessage();
        // Branch for the revision
        String branch = getBranchForRevision(repository, logEntry);
        // Logging
        logger.info(String.format("Indexing revision %d", revision));
        // Inserting or updating the revision
        revisionDao.addRevision(repository.getId(), revision, author, dateTime, message, branch);
        // Merge relationships (using a nested SVN client)
        try (Transaction ignored = transactionService.start(true)) {
            List<Long> mergedRevisions = subversionService.getMergedRevisions(repository, SVNUtils.toURL(repository.getUrl(), branch), revision);
            revisionDao.addMergedRevisions(repository.getId(), revision, mergedRevisions);
        }
        // Subversion events
        indexSVNEvents(repository, logEntry);
        // FIXME Indexes the issues
        // indexIssues(repositoryId, /*jiraConfiguration, */logEntry);
    }

    /**
     * private void indexIssues(int repositoryId, JIRAConfiguration jiraConfiguration, SVNLogEntry logEntry) {
     * long revision = logEntry.getRevision();
     * String message = logEntry.getMessage();
     * // Cache for issues
     * Set<String> revisionIssues = new HashSet<>();
     * // Gets all issues
     * // FIXME Indexation of the issues is dependent on the project!
     * Set<String> issues = jiraService.extractIssueKeysFromMessage(0, message);
     * // For each issue in the message
     * for (String issueKey : issues) {
     * // Checks that the issue has not already been associated with this revision
     * if (!revisionIssues.contains(issueKey)) {
     * revisionIssues.add(issueKey);
     * // Indexes this issue
     * issueRevisionDao.link(revision, issueKey);
     * }
     * }
     * }
     */

    private void indexSVNEvents(SVNRepository repository, SVNLogEntry logEntry) {
        indexSVNCopyEvents(repository, logEntry);
        indexSVNStopEvents(repository, logEntry);
    }

    private void indexSVNCopyEvents(SVNRepository repository, SVNLogEntry logEntry) {
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
                if (subversionService.isTagOrBranch(repository, copyToPath)) {
                    long copyFromRevision = logEntryPath.getCopyRevision();
                    logger.debug(String.format("\tCOPY %s@%d --> %s", copyFromPath, copyFromRevision, copyToPath));
                    // Adds a copy event
                    svnEventDao.createCopyEvent(repository.getId(), revision, copyFromPath, copyFromRevision, copyToPath);
                }
            }
        }
    }

    private void indexSVNStopEvents(SVNRepository repository, SVNLogEntry logEntry) {
        long revision = logEntry.getRevision();
        // Looking for copy tags
        @SuppressWarnings("unchecked")
        Map<String, SVNLogEntryPath> changedPaths = logEntry.getChangedPaths();
        // For all changes path
        for (SVNLogEntryPath logEntryPath : changedPaths.values()) {
            String path = logEntryPath.getPath();
            if (logEntryPath.getType() == SVNLogEntryPath.TYPE_DELETED && subversionService.isTagOrBranch(repository, path)) {
                logger.debug(String.format("\tSTOP %s", path));
                // Adds the stop event
                svnEventDao.createStopEvent(repository.getId(), revision, path);
            }
        }
    }

    private String getBranchForRevision(SVNRepository repository, SVNLogEntry logEntry) {
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
            return extractBranch(repository, commonPath);
        } else {
            // No path in the revision: no branch!
            return null;
        }
    }

    protected String extractBranch(SVNRepository repository, String path) {
        if (subversionService.isTrunkOrBranch(repository, path)) {
            return path;
        } else {
            String before = StringUtils.substringBeforeLast(path, "/");
            if (StringUtils.isBlank(before)) {
                return null;
            } else {
                return extractBranch(repository, before);
            }
        }
    }

    @Override
    public Runnable getTask() {
        return new Runnable() {
            @Override
            public void run() {
                // Gets all repositories
                List<SVNRepository> repositories = securityUtils.asAdmin(new Callable<List<SVNRepository>>() {
                    @Override
                    public List<SVNRepository> call() throws Exception {
                        return subversionService.getAllRepositories();
                    }
                });
                // Launches all indexations
                for (SVNRepository repository : repositories) {
                    int scanInterval = repository.getIndexationInterval();
                    if (scanInterval > 0 && extensionManager.isExtensionEnabled(SubversionExtension.EXTENSION)) {
                        indexTask(repository);
                    }
                }
            }
        };
    }

    @Override
    public Trigger getTrigger() {
        return new Trigger() {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                // Gets the mimimum of the scan intervals (outside of 0)
                Integer scanInterval = null;
                List<SVNRepository> repositories = securityUtils.asAdmin(new Callable<List<SVNRepository>>() {
                    @Override
                    public List<SVNRepository> call() throws Exception {
                        return subversionService.getAllRepositories();
                    }
                });
                for (SVNRepository repository : repositories) {
                    int interval = repository.getIndexationInterval();
                    if (interval > 0) {
                        if (scanInterval != null) {
                            scanInterval = Math.min(scanInterval, interval);
                        } else {
                            scanInterval = interval;
                        }
                    }
                }
                // No scan, tries again in one minute, in case the configuration has changed
                if (scanInterval == null || scanInterval <= 0) {
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

        private final SVNRepository repository;
        private final IndexationListener indexationListener;

        public IndexationHandler(SVNRepository repository, IndexationListener indexationListener) {
            this.repository = repository;
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
                        indexInTransaction(repository, logEntry);
                    } catch (Exception ex) {
                        logger.error("Cannot index revision " + logEntry.getRevision(), ex);
                        throw new RuntimeException(ex);
                    }
                }
            });
        }
    }

    private class DefaultIndexationJob implements IndexationJob, Runnable, IndexationListener {

        private final SVNRepository repository;
        private final long min;
        private final long max;
        private boolean running;
        private long current;

        private DefaultIndexationJob(SVNRepository repository, long min, long max) {
            this.repository = repository;
            this.min = min;
            this.max = max;
            this.current = min;
        }

        @Override
        public SVNRepository getRepository() {
            return repository;
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
                index(repository, min, max, this);
            } catch (Exception ex) {
                logger.error(String.format("Could not index range from %s to %s", min, max), ex);
            } finally {
                indexationJobs.remove(repository.getId());
            }
        }

        @Override
        public void setRevision(long revision) {
            this.current = revision;
        }
    }
}
