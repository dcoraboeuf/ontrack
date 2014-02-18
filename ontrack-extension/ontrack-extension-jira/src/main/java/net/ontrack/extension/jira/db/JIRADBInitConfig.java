package net.ontrack.extension.jira.db;

import net.ontrack.dao.AbstractDBInitConfig;
import net.sf.dbinit.DBInit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class JIRADBInitConfig extends AbstractDBInitConfig {

    public static final int VERSION = 0;

    @Autowired
    public JIRADBInitConfig(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public String getName() {
        return "ext-jira";
    }

    @Override
    public DBInit createConfig() {
        DBInit db = new DBInit();
        db.setVersion(VERSION);
        db.setJdbcDataSource(dataSource);
        db.setVersionTable("EXT_JIRA_VERSION");
        db.setVersionColumnName("VALUE");
        db.setVersionColumnTimestamp("UPDATED");
        db.setResourceInitialization("/META-INF/db/jira/init.sql");
        db.setResourceUpdate("/META-INF/db/jira/update.{0}.sql");
        return db;
    }

    @Override
    public int getOrder() {
        return 10;
    }
}
