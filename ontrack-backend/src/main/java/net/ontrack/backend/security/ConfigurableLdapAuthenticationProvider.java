package net.ontrack.backend.security;

import net.ontrack.backend.AccountService;
import net.ontrack.backend.ConfigurationCache;
import net.ontrack.backend.ConfigurationCacheKey;
import net.ontrack.backend.ConfigurationCacheSubscriber;
import net.ontrack.core.model.Account;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.service.AdminService;
import net.ontrack.service.model.LDAPConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class ConfigurableLdapAuthenticationProvider implements AuthenticationProvider, ConfigurationCacheSubscriber<LDAPConfiguration> {

    private final AdminService adminService;
    private final AccountService accountService;
    private final LdapAuthoritiesPopulator authoritiesPopulator;
    private final ConfigurationCache configurationCache;

    private LdapAuthenticationProvider ldapAuthenticationProvider;

    @Autowired
    public ConfigurableLdapAuthenticationProvider(AdminService adminService, AccountService accountService, LdapAuthoritiesPopulator authoritiesPopulator, ConfigurationCache configurationCache) {
        this.adminService = adminService;
        this.accountService = accountService;
        this.authoritiesPopulator = authoritiesPopulator;
        this.configurationCache = configurationCache;
    }

    @PostConstruct
    public void init() {
        configurationCache.subscribe(ConfigurationCacheKey.LDAP, this);
        onConfigurationChange(ConfigurationCacheKey.LDAP, adminService.getLDAPConfiguration());
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // If not enabled, cannot authenticate!
        if (ldapAuthenticationProvider == null) {
            return null;
        }
        // LDAP connection
        else {
            Authentication ldapAuthentication = ldapAuthenticationProvider.authenticate(authentication);
            if (ldapAuthentication != null && ldapAuthentication.isAuthenticated()) {
                // Gets the account
                String name = ldapAuthentication.getName();
                Account account = accountService.getAccount("ldap", name);
                if (account == null) {
                    account = new Account(0, name, name, SecurityRoles.USER, "ldap");
                }
                // OK
                return new AccountAuthentication(account);
            } else {
                return null;
            }
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    public void onConfigurationChange(ConfigurationCacheKey key, LDAPConfiguration configuration) {
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
            LdapAuthenticationProvider ldap = new LdapAuthenticationProvider(bindAuthenticator, authoritiesPopulator);
            // OK
            ldapAuthenticationProvider = ldap;
        } else {
            ldapAuthenticationProvider = null;
        }
    }
}
