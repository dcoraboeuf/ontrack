package net.ontrack.backend;

public interface ConfigurationCacheSubscriber<T> {

    void onConfigurationChange(ConfigurationCacheKey key, T value);

}
