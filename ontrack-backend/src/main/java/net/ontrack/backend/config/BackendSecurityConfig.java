package net.ontrack.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;

import java.util.List;

@Configuration
@ImportResource("classpath:META-INF/spring/backend-security.xml")
public class BackendSecurityConfig {

    @Autowired
    private List<AuthenticationProvider> providers;

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(providers);
    }

}
