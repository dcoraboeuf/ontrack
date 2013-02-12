package net.ontrack.backend.security;

import net.ontrack.backend.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class AccountServiceLdapAuthoritiesPopulator implements LdapAuthoritiesPopulator {

    @Autowired
    public AccountServiceLdapAuthoritiesPopulator(AccountService accountService) {
    }

    @Override
    public Collection<? extends GrantedAuthority> getGrantedAuthorities(DirContextOperations dirContextOperations, String s) {
        // FIXME Implement net.ontrack.backend.security.AccountServiceLdapAuthoritiesPopulator.getGrantedAuthorities
        return null;
    }

}
