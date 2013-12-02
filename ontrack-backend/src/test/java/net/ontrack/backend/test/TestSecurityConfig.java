package net.ontrack.backend.test;

import net.ontrack.core.RunProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;

import java.util.List;

@Configuration
@Profile(RunProfile.TEST)
public class TestSecurityConfig {

    @Autowired
    private List<AuthenticationProvider> providers;

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(providers);
    }

}
