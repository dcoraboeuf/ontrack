package net.ontrack.backend.security;

import net.ontrack.service.AccountService;
import net.ontrack.core.security.SecurityRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class AccountServiceLdapAuthoritiesPopulator implements LdapAuthoritiesPopulator {

    private final AccountService accountService;

    @Autowired
    public AccountServiceLdapAuthoritiesPopulator(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public Collection<? extends GrantedAuthority> getGrantedAuthorities(DirContextOperations dirContextOperations, String username) {
        // Gets the role for a user
        String role = accountService.getRole("ldap", username);
        // Default role
        if (role == null) {
            role = SecurityRoles.USER;
        }
        // OK
        return AuthorityUtils.createAuthorityList(role);
    }

}
