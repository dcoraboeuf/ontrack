package net.ontrack.extension.jira.db;

import net.sf.dbinit.DBInit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class JIRADBConfig {
	
	public static final int VERSION = 0;
	
	@Autowired
	private DataSource dataSource;
	
	@Bean
	public DBInit dbJIRAInit() {
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

}
