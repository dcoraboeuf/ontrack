package net.ontrack.backend.security;

import net.ontrack.backend.Caches;
import net.ontrack.service.AdminService;
import net.ontrack.service.model.LDAPConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.stereotype.Service;

@Service
public class DefaultLDAPProviderFactory implements LDAPProviderFactory {

    private final AdminService adminService;
    private final LdapAuthoritiesPopulator authoritiesPopulator;

    @Autowired
    public DefaultLDAPProviderFactory(AdminService adminService, LdapAuthoritiesPopulator authoritiesPopulator) {
        this.adminService = adminService;
        this.authoritiesPopulator = authoritiesPopulator;
    }

    @Cacheable(value = Caches.LDAP, key = "'0'")
    public LdapAuthenticationProvider getProvider() {
        LDAPConfiguration configuration = adminService.getLDAPConfiguration();
        if (configuration.isEnabled()) {
            // LDAP URL
            String ldapUrl = String.format("ldap://%s:%s", configuration.getHost(), configuration.getPort());
            // LDAP context
            DefaultSpringSecurityContextSource ldapContextSource = new DefaultSpringSecurityContextSource(ldapUrl);
            ldapContextSource.setUserDn(configuration.getUser());
            ldapContextSource.setPassword(configuration.getPassword());
            try {
                ldapContextSource.afterPropertiesSet();
            } catch (Exception e) {
                throw new CannotInitializeLDAPException(e);
            }
            // User search
            FilterBasedLdapUserSearch userSearch = new FilterBasedLdapUserSearch(
                    configuration.getSearchBase(),
                    configuration.getSearchFilter(),
                    ldapContextSource);
            userSearch.setSearchSubtree(true);
            // Bind authenticator
            BindAuthenticator bindAuthenticator = new BindAuthenticator(ldapContextSource);
            bindAuthenticator.setUserSearch(userSearch);
            // Provider
            return new LdapAuthenticationProvider(bindAuthenticator, authoritiesPopulator);
        } else {
            return null;
        }
    }
}
