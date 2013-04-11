package net.ontrack.backend.security;

import net.ontrack.core.model.Account;
import net.ontrack.core.model.AccountCreationForm;
import net.ontrack.core.model.ID;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.service.AccountService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
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
                // Gets the account name
                String name = ldapAuthentication.getName();
                // Gets any existing account
                Account account = accountService.getAccount("ldap", name);
                if (account == null) {
                    // If not found, auto-registers the account using the LDAP details
                    Object principal = ldapAuthentication.getPrincipal();
                    if (principal instanceof PersonLDAPUserDetails) {
                        PersonLDAPUserDetails details = (PersonLDAPUserDetails) principal;
                        // Auto-registration if email is OK
                        if (StringUtils.isNotBlank(details.getEmail())) {
                            // Registration
                            ID id = accountService.createAccount(new AccountCreationForm(
                                name,
                                details.getFullName(),
                                    details.getEmail(),
                                    SecurityRoles.USER,
                                    "ldap",
                                    "",
                                    ""
                            ));
                            // Created account
                            account = accountService.getAccount(id.getValue());
                        } else {
                            // Temporary account
                            account = new Account(0, name, details.getFullName(), "", SecurityRoles.USER, "ldap");
                        }
                    } else {
                        // Temporary account
                        account = new Account(0, name, name, "", SecurityRoles.USER, "ldap");
                    }
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
