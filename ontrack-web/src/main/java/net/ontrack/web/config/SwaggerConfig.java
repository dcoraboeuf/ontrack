package net.ontrack.web.config;

import com.mangofactory.swagger.configuration.DocumentationConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.util.Properties;

@Configuration
@Import(DocumentationConfig.class)
public class SwaggerConfig implements ServletContextAware {

    private ServletContext servletContext;

    @Bean
    public PropertySourcesPlaceholderConfigurer swaggerProperties() {

        // Swagger expects these to property values to be replaced. We don't want to propagate these to consumers of
        // this configuration, so we derive reasonable defaults here and configure the properties programmatically.
        Properties properties = new Properties();
        properties.setProperty("documentation.services.basePath", servletContext.getContextPath());
        // this property will be overridden at runtime, so the value here doesn't matter
        properties.setProperty("documentation.services.version", "REPLACE-ME");

        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setBeanName("swagger");
        configurer.setOrder(10);
        configurer.setProperties(properties);
        configurer.setIgnoreUnresolvablePlaceholders(true);
        return configurer;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
