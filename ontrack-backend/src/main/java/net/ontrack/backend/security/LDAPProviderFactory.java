package net.ontrack.backend.security;

import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;

public interface LDAPProviderFactory {

    LdapAuthenticationProvider getProvider();

}
