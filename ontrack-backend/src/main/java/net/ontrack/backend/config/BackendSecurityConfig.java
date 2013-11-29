package net.ontrack.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource("classpath:META-INF/spring/backend-security.xml")
public class BackendSecurityConfig {
}
