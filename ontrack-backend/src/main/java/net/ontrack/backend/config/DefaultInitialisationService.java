package net.ontrack.backend.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;

@Component
public class DefaultInitialisationService implements EnvInfoService {

    private final Logger logger = LoggerFactory.getLogger(DefaultInitialisationService.class);
    private final String profiles;
    private final String version;
    private final File homeDir;

    @Autowired
    public DefaultInitialisationService(
            @Value("${app.version}") String version,
            ApplicationContext ctx,
            File homeDir) {
        this.profiles = StringUtils.join(ctx.getEnvironment().getActiveProfiles(), ",");
        this.version = version;
        this.homeDir = homeDir;
    }

    @Override
    public String getProfiles() {
        return profiles;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @PostConstruct
    public void init() throws FileNotFoundException {
        logger.info("[config] With JDK:      {}", System.getProperty("java.version"));
        logger.info("[config] With profiles: {}", profiles);
        logger.info("[config] With version:  {}", version);
        logger.info("[config] With homedir:  {}", homeDir);
    }

}
