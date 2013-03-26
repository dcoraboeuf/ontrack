package net.ontrack.backend.security;

import net.ontrack.core.model.Account;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.service.AccountService;
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

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ConfigurableLdapAuthenticationProvider implements AuthenticationProvider {

    private final AdminService adminService;
    private final AccountService accountService;
    private final LdapAuthoritiesPopulator authoritiesPopulator;

    private final AtomicInteger ldapConfigurationSequence = new AtomicInteger(0);
    private LdapAuthenticationProvider ldapAuthenticationProvider;

    @Autowired
    public ConfigurableLdapAuthenticationProvider(AdminService adminService, AccountService accountService, LdapAuthoritiesPopulator authoritiesPopulator) {
        this.adminService = adminService;
        this.accountService = accountService;
        this.authoritiesPopulator = authoritiesPopulator;
    }

    protected synchronized void init() {
        LDAPConfiguration ldapConfiguration = adminService.getLDAPConfiguration();
        int sequence = ldapConfiguration.getSequence();
        if (!ldapConfigurationSequence.compareAndSet(sequence, sequence)) {
            initProvider(ldapConfiguration);
        }
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // Initialization (on demand)
        init();
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
                    account = new Account(0, name, name, "", SecurityRoles.USER, "ldap");
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

    protected synchronized void initProvider(LDAPConfiguration configuration) {
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
            ldapAuthenticationProvider = new LdapAuthenticationProvider(bindAuthenticator, authoritiesPopulator);
        } else {
            ldapAuthenticationProvider = null;
        }
    }
}
