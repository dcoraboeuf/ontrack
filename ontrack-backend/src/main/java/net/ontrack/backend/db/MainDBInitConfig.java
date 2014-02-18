package net.ontrack.backend.db;

import net.ontrack.dao.AbstractDBInitConfig;
import net.sf.dbinit.DBInit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class MainDBInitConfig extends AbstractDBInitConfig {

    public static final int VERSION = 31;

    @Autowired
    public MainDBInitConfig(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public String getName() {
        return "main";
    }

    @Override
    public DBInit createConfig() {
        DBInit db = new DBInit();
        db.setVersion(VERSION);
        db.setJdbcDataSource(dataSource);
        db.setVersionTable("DBVERSION");
        db.setVersionColumnName("VALUE");
        db.setVersionColumnTimestamp("UPDATED");
        db.setResourceInitialization("/META-INF/db/init.sql");
        db.setResourceUpdate("/META-INF/db/update.{0}.sql");
        return db;
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

}
