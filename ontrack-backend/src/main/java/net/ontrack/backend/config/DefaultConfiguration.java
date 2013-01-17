package net.ontrack.backend.config;

import javax.validation.Validator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class DefaultConfiguration {

    @Bean
    public Validator validator() {
        return new LocalValidatorFactoryBean();
    }

}
