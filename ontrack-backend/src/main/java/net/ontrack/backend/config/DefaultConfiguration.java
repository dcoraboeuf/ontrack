package net.ontrack.backend.config;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class DefaultConfiguration {

    @Bean
    public Validator validator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    @Qualifier("templating")
    public FreeMarkerConfigurationFactoryBean templateFreemarkerConfig() {
        FreeMarkerConfigurationFactoryBean f = new FreeMarkerConfigurationFactoryBean();
        f.setTemplateLoaderPath("classpath:META-INF/templates/");
        return f;
    }

}
