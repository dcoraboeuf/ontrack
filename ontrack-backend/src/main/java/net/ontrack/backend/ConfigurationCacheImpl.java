package net.ontrack.backend;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class ConfigurationCacheImpl implements ConfigurationCache {

    private final Multimap<ConfigurationCacheKey,ConfigurationCacheSubscriber<?>> subscriptions = HashMultimap.create();

    @Override
    public synchronized void putConfiguration(ConfigurationCacheKey key, Object value) {
        Collection<ConfigurationCacheSubscriber<?>> subscribers = subscriptions.get(key);
        if (subscribers != null) {
            for (ConfigurationCacheSubscriber<?> subscriber : subscribers) {
                onConfigurationChange(subscriber, key, value);
            }
        }
    }

    private <T> void onConfigurationChange(ConfigurationCacheSubscriber<T> subscriber, ConfigurationCacheKey key, Object value) {
        T t = (T) value;
        subscriber.onConfigurationChange(key, t);
    }

    @Override
    public synchronized void subscribe(ConfigurationCacheKey key, ConfigurationCacheSubscriber<?> subscriber) {
        subscriptions.put(key, subscriber);
    }
}
