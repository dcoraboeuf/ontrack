package net.ontrack.web.config;

import com.netbeetle.jackson.ObjectMapperFactory;
import freemarker.cache.TemplateLoader;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.extension.api.ExtensionManager;
import net.ontrack.service.SubscriptionService;
import net.ontrack.web.locale.LocaleInterceptor;
import net.ontrack.web.support.DefaultErrorHandlingMultipartResolver;
import net.ontrack.web.support.WebInterceptor;
import net.ontrack.web.support.fm.*;
import net.ontrack.web.support.json.LocalTimeDeserializer;
import net.ontrack.web.support.json.LocalTimeSerializer;
import net.sf.jstring.Strings;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ext.JodaDeserializers.LocalDateDeserializer;
import org.codehaus.jackson.map.ext.JodaDeserializers.LocalDateTimeDeserializer;
import org.codehaus.jackson.map.ext.JodaSerializers.LocalDateSerializer;
import org.codehaus.jackson.map.ext.JodaSerializers.LocalDateTimeSerializer;
import org.codehaus.jackson.map.module.SimpleModule;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebMvc
@PropertySource("/META-INF/strings/core.properties")
public class WebConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private Environment env;

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private ExtensionManager extensionManager;
    
    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private Strings strings;

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.clear();
        // Plain text
        converters.add(new StringHttpMessageConverter());
        // JSON
        MappingJacksonHttpMessageConverter mapper = new MappingJacksonHttpMessageConverter();
        mapper.setObjectMapper(jacksonObjectMapper());
        converters.add(mapper);
    }

    @Bean
    public Object exporter() throws IOException {
        MBeanExporter exporter = new MBeanExporter();
        exporter.setBeans(Collections.<String, Object>singletonMap("bean:name=strings", strings));
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
        registry.addResourceHandler("/extension/**").addResourceLocations("classpath:/META-INF/extension/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LocaleInterceptor(strings));
        registry.addInterceptor(new WebInterceptor(strings));
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorPathExtension(false).favorParameter(true);
    }

    @Bean
    public FreeMarkerConfig freemarkerConfig() {
        // Configurer
        FreeMarkerConfigurer c = new FreeMarkerConfigurer();
        c.setTemplateLoaderPaths(new String[]{
                "/WEB-INF/views"
        });
        // Extension views
        c.setPostTemplateLoaders(new TemplateLoader[]{
                new ExtensionTemplateLoader(extensionManager)
        });
        // Freemarker variables
        Map<String, Object> variables = new HashMap<>();
        variables.put("loc", new FnLoc(strings));
        variables.put("locSelected", new FnLocSelected());
        variables.put("locFormatDate", new FnLocFormatDate(strings));
        variables.put("locFormatTime", new FnLocFormatTime(strings));
        variables.put("secLogged", new FnSecLogged(securityUtils));
        variables.put("secAccountId", new FnSecAccountId(securityUtils));
        variables.put("secAdmin", new FnSecAdmin(securityUtils));
        variables.put("secDisplayName", new FnSecDisplayName(securityUtils));
        variables.put("secSubscriber", new FnSecSubscriber(securityUtils, subscriptionService));
        variables.put("subscribed", new FnSubscribed(securityUtils, subscriptionService));
        // Extensions
        variables.put("extensionTopLevelActions", new FnExtensionTopLevelActions(strings, extensionManager, securityUtils));
        variables.put("extensionDiffActions", new FnExtensionDiffActions(strings, extensionManager, securityUtils));
        // OK
        c.setFreemarkerVariables(variables);
        // OK
        return c;
    }

    @Bean
    public ViewResolver freemarkerViewResolver() {
        FreeMarkerViewResolver o = new FreeMarkerViewResolver();
        o.setCache(false);
        o.setPrefix("");
        o.setSuffix(".html");
        return o;
    }

    @Bean
    public View jsonViewResolver() {
        MappingJacksonJsonView o = new MappingJacksonJsonView();
        o.setObjectMapper(jacksonObjectMapper());
        return o;
    }

    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver o = new DefaultErrorHandlingMultipartResolver();
        o.setMaxUploadSize(4 * 1024); // 4K limit
        return o;
    }

    @Bean
    public ObjectMapper jacksonObjectMapper() {
        ObjectMapper mapper = ObjectMapperFactory.createObjectMapper();

        jsonJoda(mapper);

        return mapper;
    }

    protected void jsonJoda(ObjectMapper mapper) {
        SimpleModule jodaModule = new SimpleModule("JodaTimeModule", new Version(1, 0, 0, null));
        jsonLocalDateTime(jodaModule);
        jsonLocalDate(jodaModule);
        jsonLocalTime(jodaModule);
        mapper.registerModule(jodaModule);
    }

    protected void jsonLocalDateTime(SimpleModule jodaModule) {
        jodaModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        jodaModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
    }

    protected void jsonLocalTime(SimpleModule jodaModule) {
        jodaModule.addSerializer(LocalTime.class, new LocalTimeSerializer());
        jodaModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer());
    }

    protected void jsonLocalDate(SimpleModule jodaModule) {
        jodaModule.addSerializer(LocalDate.class, new LocalDateSerializer());
        jodaModule.addDeserializer(LocalDate.class, new LocalDateDeserializer());
    }

}
