package net.ontrack.backend.db;

import javax.naming.NamingException;
import javax.sql.DataSource;

import net.ontrack.core.RunProfile;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jndi.JndiObjectFactoryBean;

@Configuration
@Profile({RunProfile.IT, RunProfile.DEV, RunProfile.PROD})
public class DataSourceConfig {
	
	@Bean
	public DataSource dataSource() throws IllegalArgumentException, NamingException {
		JndiObjectFactoryBean factory = new JndiObjectFactoryBean();
		factory.setExpectedType(DataSource.class);
		factory.setJndiName("java:comp/env/jdbc/ontrack");
		factory.afterPropertiesSet();
		DataSource dataSource = (DataSource) factory.getObject();
		return dataSource;
	}

}
