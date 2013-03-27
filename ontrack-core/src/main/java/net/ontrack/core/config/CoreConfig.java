package net.ontrack.core.config;

import net.sf.jstring.Strings;
import net.sf.jstring.support.StringsLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;

@Configuration
public class CoreConfig {

    @Bean
    public Strings strings() {
        return StringsLoader.auto(Locale.ENGLISH, Locale.FRENCH);
    }

}
