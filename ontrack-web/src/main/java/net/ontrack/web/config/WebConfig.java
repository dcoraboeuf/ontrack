package net.ontrack.web.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.ontrack.web.support.fm.FnLoc;
import net.ontrack.web.support.fm.FnLocFormatDate;
import net.ontrack.web.support.fm.FnLocFormatTime;
import net.ontrack.web.support.fm.FnLocSelected;
import net.sf.jstring.Strings;
import net.sf.jstring.support.StringsLoader;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

@Configuration
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {
	
	// TODO Moves this to the core
	@Bean
	public Strings strings() throws IOException {
		return StringsLoader.auto(Locale.ENGLISH, Locale.FRENCH);
	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}
	
	@Bean
	public FreeMarkerConfig freemarkerConfig() throws IOException {
		FreeMarkerConfigurer c = new FreeMarkerConfigurer();
		c.setTemplateLoaderPath("/WEB-INF/views");
		// Freemarker variables		
		Map<String, Object> variables = new HashMap<>();
		variables.put("loc", new FnLoc(strings()));
		variables.put("locSelected", new FnLocSelected());
		variables.put("locFormatDate", new FnLocFormatDate(strings()));
		variables.put("locFormatTime", new FnLocFormatTime(strings()));
		c.setFreemarkerVariables(variables);
		// OK
		return c;
	}
	
	@Bean
	public ViewResolver viewResolver() {
		FreeMarkerViewResolver o = new FreeMarkerViewResolver();
		o.setCache(false);
		o.setPrefix("");
		o.setSuffix(".html");
		return o;
	}

}
