package net.ontrack.backend.config;

import net.ontrack.core.RunProfile;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jndi.JndiObjectFactoryBean;

import javax.naming.NamingException;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
@Profile(RunProfile.PROD)
public class ProdEnvironmentConfig implements EnvironmentConfig {

    private static final String VARIABLE_PATTERN = "\\%\\{([a-zA-Z_\\-\\.0-9]+)\\}";

    public static String expandPath(String path) {
        Matcher m = Pattern.compile(VARIABLE_PATTERN).matcher(path);
        StringBuffer s = new StringBuffer();
        while (m.find()) {
            String variable = m.group(1);
            String replacement = expandVariable(variable);
            m.appendReplacement(s, replacement);
        }
        m.appendTail(s);
        return s.toString();
    }

    public static String expandVariable(String variable) {
        String sysVar = System.getProperty(variable);
        if (StringUtils.isNotBlank(sysVar)) {
            return sysVar;
        }
        String envVar = System.getenv(variable);
        if (StringUtils.isNotBlank(envVar)) {
            return envVar;
        }
        throw new IllegalStateException("Cannot expand variable: " + variable);
    }

    @Bean
    @Override
    public File homeDir() {
        // 1) System property
        String sysPath = System.getProperty("ontrack.home");
        if (StringUtils.isNotBlank(sysPath)) {
            return new File(expandPath(sysPath));
        }
        // 2) Environment
        String envPath = System.getenv("ONTRACK_HOME");
        if (StringUtils.isNotBlank(envPath)) {
            return new File(expandPath(envPath));
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
