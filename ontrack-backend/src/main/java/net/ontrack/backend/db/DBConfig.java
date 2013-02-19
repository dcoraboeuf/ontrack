package net.ontrack.backend.db;

import net.sf.dbinit.DBInit;
import net.sf.dbinit.DBInitAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.List;

@Configuration
public class DBConfig {
	
	public static final int VERSION = 8;
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired(required = false)
	private List<DBInitAction> postActions;
	
	@Bean
	public DBInit dbInit() {
		DBInit db = new DBInit();
		db.setVersion(VERSION);
		db.setJdbcDataSource(dataSource);
		db.setVersionTable("DBVERSION");
		db.setVersionColumnName("VALUE");
		db.setVersionColumnTimestamp("UPDATED");
		db.setResourceInitialization("/META-INF/db/init.sql");
		db.setResourceUpdate("/META-INF/db/update.{0}.sql");
		if (postActions != null) {
			db.setPostActions(postActions);
		}
		return db;
	}

}
