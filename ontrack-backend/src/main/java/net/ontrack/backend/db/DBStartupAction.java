package net.ontrack.backend.db;

import net.sf.dbinit.DBExecutor;
import net.sf.dbinit.DBInitAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Component
public class DBStartupAction implements DBInitAction {

    private final List<StartupService> startupServices;

    @Autowired
    public DBStartupAction(List<StartupService> startupServices) {
        this.startupServices = startupServices;
    }

    @Override
    public void run(DBExecutor executor, Connection connection) throws SQLException {
        for (StartupService startupService : startupServices) {
            startupService.start();
        }
    }
}
