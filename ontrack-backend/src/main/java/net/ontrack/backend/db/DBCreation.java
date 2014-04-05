package net.ontrack.backend.db;

import net.ontrack.dao.DBInitConfig;
import net.ontrack.service.StartupService;
import net.sf.dbinit.DBInit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Creation or update of the database.
 */
@Service
public class DBCreation {

    private final Logger logger = LoggerFactory.getLogger(DBCreation.class);

    private final List<DBInitConfig> dbInitConfigs;
    private final List<StartupService> startupServices;

    @Autowired
    public DBCreation(List<DBInitConfig> dbInitConfigs, List<StartupService> startupServices) {
        // Sorts the DB configurations
        this.dbInitConfigs = new ArrayList<>(dbInitConfigs);
        Collections.sort(
                this.dbInitConfigs,
                new Comparator<DBInitConfig>() {
                    @Override
                    public int compare(DBInitConfig o1, DBInitConfig o2) {
                        return o2.getOrder() - o1.getOrder();
                    }
                }
        );
        // Sorts the startup services
        List<StartupService> services = new ArrayList<>(startupServices);
        Collections.sort(services, new Comparator<StartupService>() {
            @Override
            public int compare(StartupService o1, StartupService o2) {
                return o1.startupOrder() - o2.startupOrder();
            }
        });
        this.startupServices = services;
    }

    /**
     * Runs all database configurations and runs each {@link net.ontrack.service.StartupService} in turn.
     */
    @PostConstruct
    public void init() {

        logger.info("[db] DB initialisation.");
        for (DBInitConfig dbInitConfig : dbInitConfigs) {
            logger.info("[db] DB initialisation for \"{}\"", dbInitConfig.getName());
            DBInit dbInit = dbInitConfig.createConfig();
            dbInit.run();
        }

        logger.info("[startup] Running startup services");
        for (StartupService startupService : startupServices) {
            logger.info("[startup] Starting service \"{}\"", startupService.getName());
            startupService.start();
        }

    }

}
