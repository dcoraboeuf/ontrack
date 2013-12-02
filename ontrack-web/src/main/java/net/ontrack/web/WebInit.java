package net.ontrack.web;

import net.ontrack.web.config.WebConfig;
import net.ontrack.web.config.WebSecurityConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

@Order(1)
public class WebInit extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{
                WebConfig.class
        };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{
                EmptyConfig.class
        };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/*"};
    }

    @Configuration
    public static class EmptyConfig {

    }
}
