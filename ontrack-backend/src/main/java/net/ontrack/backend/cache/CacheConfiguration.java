package net.ontrack.backend.cache;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@EnableCaching
public class CacheConfiguration {

    @Bean
    public CacheManager cacheManager() throws Exception {
        SimpleCacheManager o = new SimpleCacheManager();
        o.setCaches(
                Arrays.asList(
                        new GuavaCacheFactoryBean(Caches.ACCOUNT, 100, 600).getObject(),
                        new GuavaCacheFactoryBean(Caches.PROJECT, 10, 60).getObject(),
                        new GuavaCacheFactoryBean(Caches.BRANCH, 50, 60).getObject(),
                        new GuavaCacheFactoryBean(Caches.VALIDATION_STAMP, 100, 60).getObject(),
                        new GuavaCacheFactoryBean(Caches.BUILD, 200, 60).getObject(),
                        new GuavaCacheFactoryBean(Caches.CONFIGURATION_KEY, 50, 60).getObject(),
                        new GuavaCacheFactoryBean(Caches.CONFIGURATION, 3, 600).getObject(),
                        new GuavaCacheFactoryBean(Caches.MAIL, 1, 600).getObject(),
                        new GuavaCacheFactoryBean(Caches.LDAP, 1, 600).getObject()
                )
        );
        return o;
    }

}
