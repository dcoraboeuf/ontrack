package net.ontrack.backend.db;

import net.ontrack.service.StartupService;
import net.sf.dbinit.DBExecutor;
import net.sf.dbinit.DBInitAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Component
public class DBStartupAction implements DBInitAction {

    private final Logger logger = LoggerFactory.getLogger(DBStartupAction.class);

    private final List<StartupService> startupServices;

    @Autowired
    public DBStartupAction(List<StartupService> startupServices) {
        // Sorts on the order
        List<StartupService> services = new ArrayList<>(startupServices);
        Collections.sort(services, new Comparator<StartupService>() {
            @Override
            public int compare(StartupService o1, StartupService o2) {
                return o1.startupOrder() - o2.startupOrder();
            }
        });
        this.startupServices = services;
    }

    @Override
    public void run(DBExecutor executor, Connection connection) throws SQLException {
        logger.info("[startup] Starting services");
        for (StartupService startupService : startupServices) {
            logger.info("[startup] Starting {}", startupService);
            startupService.start();
        }
    }
}
