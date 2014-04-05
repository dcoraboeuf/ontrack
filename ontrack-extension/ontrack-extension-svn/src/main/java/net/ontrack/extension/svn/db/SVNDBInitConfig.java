package net.ontrack.extension.svn.db;

import net.ontrack.dao.AbstractDBInitConfig;
import net.sf.dbinit.DBInit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class SVNDBInitConfig extends AbstractDBInitConfig {

    public static final int VERSION = 0;

    @Autowired
    public SVNDBInitConfig(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public String getName() {
        return "ext-svn";
    }

    @Override
    public DBInit createConfig() {
        DBInit db = new DBInit();
        db.setVersion(VERSION);
        db.setJdbcDataSource(dataSource);
        db.setVersionTable("EXT_SVN_VERSION");
        db.setVersionColumnName("VALUE");
        db.setVersionColumnTimestamp("UPDATED");
        db.setResourceInitialization("/META-INF/db/subversion/init.sql");
        db.setResourceUpdate("/META-INF/db/subversion/update.{0}.sql");
        return db;
    }

    @Override
    public int getOrder() {
        return 20;
    }
}
