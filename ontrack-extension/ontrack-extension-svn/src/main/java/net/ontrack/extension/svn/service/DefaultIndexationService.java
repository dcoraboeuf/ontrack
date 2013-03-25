package net.ontrack.extension.svn.service;

import net.ontrack.core.security.SecurityRoles;
import net.ontrack.extension.svn.IndexationConfigurationExtension;
import net.ontrack.extension.svn.IndexationService;
import net.ontrack.service.api.ScheduledService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class DefaultIndexationService implements IndexationService, ScheduledService {

    private final Logger logger = LoggerFactory.getLogger(IndexationService.class);
    private final IndexationConfigurationExtension configurationExtension;
    // Current indexation
    private final AtomicReference<IndexationJob> currentIndexationJob = new AtomicReference<>();

    @Autowired
    public DefaultIndexationService(IndexationConfigurationExtension configurationExtension) {
        this.configurationExtension = configurationExtension;
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

    protected boolean isIndexationRunning() {
        IndexationJob job = currentIndexationJob.get();
        return job != null && job.isRunning();
    }

    @Override
    @Secured(SecurityRoles.ADMINISTRATOR)
    public void indexFromLatest() {
//        // Loads the repository information
//        SVNURL url = SVNUtils.toURL(subversionConfiguration.getUrl());
//        // Opens a SVN transaction
//        Transaction transaction = tx();
//        try {
//            // Last scanned revision
//            long lastScannedRevision = repositoryService.getLastScannedRevision();
//            if (lastScannedRevision <= 0) {
//                lastScannedRevision = indexationConfiguration.getStartRevision();
//            }
//            // Logging
//            logger.info("Submitting indexation from latest scanned revision: " + lastScannedRevision);
//            // HEAD revision
//            long repositoryRevision = svnService.getRepositoryRevision(url);
//            // Request index of the range
//            indexRange(lastScannedRevision + 1, repositoryRevision);
//        } finally {
//            transaction.end();
//        }
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
//        DefaultIndexationJob job = new DefaultIndexationJob(min, max);
//        currentIndexationJob.set(job);
//        // Schedule the scan
//        executor.submit(job);
    }

    @Override
    public Runnable getTask() {
        return new Runnable() {
            @Override
            public void run() {
                indexTask();
            }
        };
    }

    @Override
    public Trigger getTrigger() {
        return new Trigger() {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                // Configuration
                int scanInterval = configurationExtension.getConfiguration().getScanInterval();
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
}
