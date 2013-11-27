package net.ontrack.backend.config;

import net.ontrack.core.support.MapBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.method.MapBasedMethodSecurityMetadataSource;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableGlobalMethodSecurity
public class BackendSecurityConfig extends GlobalMethodSecurityConfiguration {

    private final MapBasedMethodSecurityMetadataSource mapBasedMethodSecurityMetadataSource = new MapBasedMethodSecurityMetadataSource();

    @Autowired
    private AccessDecisionManager accessDecisionManager;

    @Override
    protected AccessDecisionManager accessDecisionManager() {
        return accessDecisionManager;
    }

    @Override
    protected MapBasedMethodSecurityMetadataSource customMethodSecurityMetadataSource() {
        return mapBasedMethodSecurityMetadataSource;
    }

    @Bean
    public BeanPostProcessor protectPointcutPostProcessor() {
        BackendProtectPointcutPostProcessor processor = new BackendProtectPointcutPostProcessor(mapBasedMethodSecurityMetadataSource);
        processor.setPointcutMap(
                MapBuilder.<String, List<ConfigAttribute>>create()
                        .with("execution(@net.ontrack.core.security.ProjectGrant * net.ontrack.backend.*.*(..))", Arrays.<ConfigAttribute>asList(new SecurityConfig("project")))
                        .with("execution(@net.ontrack.core.security.GlobalGrant * net.ontrack.backend.*.*(..))", Arrays.<ConfigAttribute>asList(new SecurityConfig("global")))
                        .get()
        );
        return processor;
    }
}
