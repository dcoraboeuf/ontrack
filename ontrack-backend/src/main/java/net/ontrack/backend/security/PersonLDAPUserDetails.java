package net.ontrack.backend.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapUserDetails;

import java.util.Collection;

public class PersonLDAPUserDetails implements LdapUserDetails {

    private final LdapUserDetails support;
    private final String fullName;
    private final String email;

    public PersonLDAPUserDetails(LdapUserDetails support, String fullName, String email) {
        this.support = support;
        this.fullName = fullName;
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getDn() {
        return support.getDn();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return support.getAuthorities();
    }

    @Override
    public String getPassword() {
        return support.getPassword();
    }

    @Override
    public String getUsername() {
        return support.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return support.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return support.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return support.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return support.isEnabled();
    }
}
