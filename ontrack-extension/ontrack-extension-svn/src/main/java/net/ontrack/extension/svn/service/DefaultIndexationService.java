package net.ontrack.extension.svn.service;

import net.ontrack.extension.svn.IndexationConfigurationExtension;
import net.ontrack.extension.svn.IndexationService;
import net.ontrack.service.api.ScheduledService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class DefaultIndexationService implements IndexationService, ScheduledService {

    private final Logger logger = LoggerFactory.getLogger(IndexationService.class);
    private final IndexationConfigurationExtension configurationExtension;

    @Autowired
    public DefaultIndexationService(IndexationConfigurationExtension configurationExtension) {
        this.configurationExtension = configurationExtension;
    }

    public void indexTask() {
        logger.info("[indexation] Indexation task starting...");
        logger.info("[indexation] Indexation task stopped.");
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
