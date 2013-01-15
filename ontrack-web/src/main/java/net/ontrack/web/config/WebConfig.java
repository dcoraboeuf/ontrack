package net.ontrack.web.config;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.ontrack.web.support.WebInterceptor;
import net.ontrack.web.support.fm.FnLoc;
import net.ontrack.web.support.fm.FnLocFormatDate;
import net.ontrack.web.support.fm.FnLocFormatTime;
import net.ontrack.web.support.fm.FnLocSelected;
import net.sf.jstring.Strings;
import net.sf.jstring.support.StringsLoader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

@Configuration
@EnableWebMvc
@PropertySource("/META-INF/strings/core.properties")
public class WebConfig extends WebMvcConfigurerAdapter {

	@Autowired
	private Environment env;

	// TODO Moves this to the core
	@Bean
	public Strings strings() throws IOException {
		return StringsLoader.auto(Locale.ENGLISH, Locale.FRENCH);
	}
	
	@Bean
	public Object exporter() throws IOException {
		MBeanExporter exporter = new MBeanExporter();
		exporter.setBeans(Collections.<String,Object>singletonMap("bean:name=strings", strings()));
		return exporter;
	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		String version = env.getProperty("app.version");
		registry.addResourceHandler(String.format("/resources/v%s/**", version)).addResourceLocations("/static/");
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new WebInterceptor());
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
