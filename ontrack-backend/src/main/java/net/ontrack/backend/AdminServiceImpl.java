package net.ontrack.backend;

import net.ontrack.service.AdminService;
import net.ontrack.service.EventService;
import net.ontrack.service.model.LDAPConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import javax.validation.Validator;

@Service
public class AdminServiceImpl extends AbstractServiceImpl implements AdminService {

    private final ConfigurationService configurationService;

    @Autowired
    public AdminServiceImpl(DataSource dataSource, Validator validator, EventService eventService, ConfigurationService configurationService) {
        super(dataSource, validator, eventService);
        this.configurationService = configurationService;
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
}
