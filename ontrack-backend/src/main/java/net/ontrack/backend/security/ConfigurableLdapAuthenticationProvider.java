package net.ontrack.backend.security;

import net.ontrack.core.model.Account;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.stereotype.Service;

@Service
public class ConfigurableLdapAuthenticationProvider implements AuthenticationProvider {

    private final AccountService accountService;
    private final LDAPProviderFactory ldapProviderFactory;

    @Autowired
    public ConfigurableLdapAuthenticationProvider(AccountService accountService, LDAPProviderFactory ldapProviderFactory) {
        this.accountService = accountService;
        this.ldapProviderFactory = ldapProviderFactory;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // Gets the (cached) provider
        LdapAuthenticationProvider ldapAuthenticationProvider = ldapProviderFactory.getProvider();
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
}
