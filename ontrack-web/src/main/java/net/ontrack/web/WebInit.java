package net.ontrack.web;

import net.ontrack.web.config.WebConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

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
