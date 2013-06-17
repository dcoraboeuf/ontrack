package net.ontrack.backend.config;

import net.ontrack.core.RunProfile;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jndi.JndiObjectFactoryBean;

import javax.naming.NamingException;
import java.io.File;

@Configuration
@Profile(RunProfile.PROD)
public class ProdEnvironmentConfig implements EnvironmentConfig {

    @Bean
    @Override
    public File homeDir() {
        // 1) System property
        String sysPath = System.getProperty("ontrack.home");
        if (StringUtils.isNotBlank(sysPath)) {
            return new File(sysPath);
        }
        // 2) Environment
        String envPath = System.getenv("ONTRACK_HOME");
        if (StringUtils.isNotBlank(envPath)) {
            return new File(envPath);
        }
        // 3) JNDI
        JndiObjectFactoryBean factory = new JndiObjectFactoryBean();
        factory.setExpectedType(File.class);
        factory.setJndiName("file/home");
        try {
            factory.afterPropertiesSet();
        } catch (NamingException e) {
            throw new RuntimeException("Cannot find home directory in JNDI at file/home");
        }
        return (File) factory.getObject();
    }
}
