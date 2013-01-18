package net.ontrack.backend.config;

import java.io.IOException;
import java.util.Locale;

import javax.sql.DataSource;

import net.ontrack.core.RunProfile;
import net.sf.jstring.Strings;
import net.sf.jstring.support.StringsLoader;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile(RunProfile.TEST)
public class UnitTestConfiguration {

	private static final Logger log = LoggerFactory.getLogger(UnitTestConfiguration.class);
	
	@Bean
	public Strings strings() throws IOException {
		return StringsLoader.auto(Locale.ENGLISH, Locale.FRENCH);
	}

	@Bean
	public DataSource dataSource() {
		String dbURL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
		log.info("Using database at {}", dbURL);
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName("org.h2.Driver");
		ds.setUrl(dbURL);
		ds.setUsername("sa");
		ds.setPassword("");
		ds.setDefaultAutoCommit(false);
		ds.setInitialSize(1);
		ds.setMaxActive(2);
		return ds;
	}

}
