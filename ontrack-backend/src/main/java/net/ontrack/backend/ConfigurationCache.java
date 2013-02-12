package net.ontrack.backend;

public interface ConfigurationCache {

    void putConfiguration(ConfigurationCacheKey key, Object value);

    void subscribe (ConfigurationCacheKey key, ConfigurationCacheSubscriber<?> subscriber);

}
