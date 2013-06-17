package net.ontrack.backend.config;

import net.ontrack.core.RunProfile;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.File;

@Configuration
@Profile({RunProfile.IT, RunProfile.TEST})
public class TestEnvironmentConfig implements EnvironmentConfig {

    @Override
    @Bean
    public File homeDir() {
        return new File("target/work/root");
    }
}
