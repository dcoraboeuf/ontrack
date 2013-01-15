package net.ontrack.web.config;

import net.ontrack.web.locale.LocaleInterceptor;
import net.sf.jstring.Strings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

@Configuration
public class LocaleConfig {

    @Autowired
    private Strings strings;
	
	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		return new LocaleInterceptor(strings);
	}
	
	@Bean
	public LocaleResolver localeResolver () {
		return new CookieLocaleResolver();
	}

}
