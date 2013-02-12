package net.ontrack.backend;

public interface ConfigurationService {

    String get(ConfigurationKey key, boolean required, String defaultValue);

    Integer getInteger(ConfigurationKey key, boolean required, int defaultValue);

    boolean getBoolean(ConfigurationKey key, boolean required, boolean defaultValue);

}
