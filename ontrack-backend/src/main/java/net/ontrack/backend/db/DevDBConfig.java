package net.ontrack.backend.db;

import javax.sql.DataSource;

import net.ontrack.core.RunProfile;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({RunProfile.TEST, RunProfile.IT, RunProfile.DEV})
public class DevDBConfig {
	
	@Bean
	public DataSource dataSource() {
		// FIXME DevDBConfig.dataSource
		return null;
	}
	
  	/*
  	<beans profile="it,dev,prod">
    	<jee:jndi-lookup id="dataSource" jndi-name="jdbc/iteach" />
  	</beans>
  	*/

}
