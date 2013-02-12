package net.ontrack.backend;

import net.ontrack.backend.db.SQL;
import net.ontrack.service.EventService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import javax.validation.Validator;

@Service
public class ConfigurationServiceImpl extends AbstractServiceImpl implements ConfigurationService {

    @Autowired
    public ConfigurationServiceImpl(DataSource dataSource, Validator validator, EventService eventService) {
        super(dataSource, validator, eventService);
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
        return getFirstItem(SQL.CONFIGURATION_GET, params("name", key.name()), String.class);
    }
}
