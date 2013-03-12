package net.ontrack.backend;

import net.ontrack.core.security.SecurityRoles;
import net.ontrack.service.AdminService;
import net.ontrack.service.EventService;
import net.ontrack.service.model.LDAPConfiguration;
import net.ontrack.service.validation.LDAPConfigurationValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Validator;

@Service
public class AdminServiceImpl extends AbstractServiceImpl implements AdminService {

    private final ConfigurationService configurationService;
    private final ConfigurationCache configurationCache;

    @Autowired
    public AdminServiceImpl(Validator validator, EventService eventService, ConfigurationService configurationService, ConfigurationCache configurationCache) {
        super(validator, eventService);
        this.configurationService = configurationService;
        this.configurationCache = configurationCache;
    }

    @Override
    @Transactional(readOnly = true)
    public LDAPConfiguration getLDAPConfiguration() {
        LDAPConfiguration c = new LDAPConfiguration();
        boolean enabled = configurationService.getBoolean(ConfigurationKey.LDAP_ENABLED, false, false);
        c.setEnabled(enabled);
        if (enabled) {
            c.setHost(configurationService.get(ConfigurationKey.LDAP_HOST, true, null));
            c.setPort(configurationService.getInteger(ConfigurationKey.LDAP_PORT, true, 0));
            c.setSearchBase(configurationService.get(ConfigurationKey.LDAP_SEARCH_BASE, true, null));
            c.setSearchFilter(configurationService.get(ConfigurationKey.LDAP_SEARCH_FILTER, true, null));
            c.setUser(configurationService.get(ConfigurationKey.LDAP_USER, true, null));
            c.setPassword(configurationService.get(ConfigurationKey.LDAP_PASSWORD, true, null));
        } else {
            // Default values
            c.setPort(389);
        }
        // OK
        return c;
    }

    @Override
    @Transactional
    @Secured(SecurityRoles.ADMINISTRATOR)
    public void saveLDAPConfiguration(LDAPConfiguration configuration) {
        // Validation
        validate(configuration, LDAPConfigurationValidation.class);
        // Saving...
        configurationService.set(ConfigurationKey.LDAP_ENABLED, configuration.isEnabled());
        if (configuration.isEnabled()) {
            configurationService.set(ConfigurationKey.LDAP_HOST, configuration.getHost());
            configurationService.set(ConfigurationKey.LDAP_PORT, configuration.getPort());
            configurationService.set(ConfigurationKey.LDAP_SEARCH_BASE, configuration.getSearchBase());
            configurationService.set(ConfigurationKey.LDAP_SEARCH_FILTER, configuration.getSearchFilter());
            configurationService.set(ConfigurationKey.LDAP_USER, configuration.getUser());
            configurationService.set(ConfigurationKey.LDAP_PASSWORD, configuration.getPassword());
        }
        // Notifies the configuration listeners
        configurationCache.putConfiguration(ConfigurationCacheKey.LDAP, configuration);
    }
}
