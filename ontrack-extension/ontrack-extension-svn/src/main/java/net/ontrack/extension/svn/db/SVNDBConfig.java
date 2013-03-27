package net.ontrack.extension.svn.db;

import net.sf.dbinit.DBInit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class SVNDBConfig {
	
	public static final int VERSION = 0;
	
	@Autowired
	private DataSource dataSource;
	
	@Bean
	public DBInit dbSubversionInit() {
		DBInit db = new DBInit();
		db.setVersion(VERSION);
		db.setJdbcDataSource(dataSource);
		db.setVersionTable("SVNVERSION");
		db.setVersionColumnName("VALUE");
		db.setVersionColumnTimestamp("UPDATED");
		db.setResourceInitialization("/META-INF/db/subversion/init.sql");
		db.setResourceUpdate("/META-INF/db/subversion/update.{0}.sql");
		return db;
	}

}
