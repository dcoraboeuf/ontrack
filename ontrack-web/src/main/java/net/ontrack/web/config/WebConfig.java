package net.ontrack.web.config;

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

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}
	
	@Bean
	public FreeMarkerConfig freemarkerConfig() {
		FreeMarkerConfigurer c = new FreeMarkerConfigurer();
		c.setTemplateLoaderPath("/WEB-INF/views");
		return c;
	}
	
	/*
	<!-- FIXME Uses profiles for caching -->
	<bean id="viewResolver" class="org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver">
		<property name="cache" value="false" />
		<property name="prefix" value="" />
		<property name="suffix" value=".html" />
	</bean>
	*/
	@Bean
	public ViewResolver viewResolver() {
		FreeMarkerViewResolver o = new FreeMarkerViewResolver();
		o.setCache(false);
		o.setPrefix("");
		o.setSuffix(".html");
		return o;
	}

}
