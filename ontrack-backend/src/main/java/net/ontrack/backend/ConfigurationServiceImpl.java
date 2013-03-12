package net.ontrack.backend;

import net.ontrack.backend.dao.ConfigurationDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfigurationServiceImpl implements ConfigurationService {

    private final ConfigurationDao configurationDao;

    @Autowired
    public ConfigurationServiceImpl(ConfigurationDao configurationDao) {
        this.configurationDao = configurationDao;
    }

    @Override
    @Transactional(readOnly = true)
    public String get(ConfigurationKey key, boolean required, String defaultValue) {
        String value = get(key);
        if (StringUtils.isBlank(value)) {
            if (required) {
                throw new ConfigurationKeyMissingException(key);
            } else {
                return defaultValue;
            }
        } else {
            return value;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getInteger(ConfigurationKey key, boolean required, int defaultValue) {
        String value = get(key, required, String.valueOf(defaultValue));
        return Integer.parseInt(value, 10);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean getBoolean(ConfigurationKey key, boolean required, boolean defaultValue) {
        String value = get(key, required, String.valueOf(defaultValue));
        return Boolean.parseBoolean(value);
    }

    protected String get(ConfigurationKey key) {
        return configurationDao.getValue(key.name());
    }

    @Override
    @Transactional
    public void set(ConfigurationKey key, boolean value) {
        set(key, String.valueOf(value));
    }

    @Override
    @Transactional
    public void set(ConfigurationKey key, int value) {
        set(key, String.valueOf(value));
    }

    @Override
    @Transactional
    public void set(ConfigurationKey key, String value) {
        configurationDao.setValue(key.name(), value);
    }
}
