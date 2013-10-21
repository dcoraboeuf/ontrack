package net.ontrack.backend.security;

import net.ontrack.backend.cache.Caches;
import net.ontrack.service.AdminService;
import net.ontrack.service.model.LDAPConfiguration;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.regex.Pattern;

@Service
public class DefaultLDAPProviderFactory implements LDAPProviderFactory {

    private final Pattern cnPattern = Pattern.compile("cn=(.*)", Pattern.CASE_INSENSITIVE);

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
            LdapAuthenticationProvider ldapAuthenticationProvider = new LdapAuthenticationProvider(bindAuthenticator, authoritiesPopulator);
            ldapAuthenticationProvider.setUserDetailsContextMapper(new ConfigurableUserDetailsContextMapper());
            // OK
            return ldapAuthenticationProvider;
        } else {
            return null;
        }
    }

    private class ConfigurableUserDetailsContextMapper extends LdapUserDetailsMapper {

        @Override
        public UserDetails mapUserFromContext(DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> authorities) {
            // Gets the configuration
            LDAPConfiguration configuration = adminService.getLDAPConfiguration();
            // Default details
            LdapUserDetails userDetails = (LdapUserDetails) super.mapUserFromContext(ctx, username, authorities);
            // Full name
            String fullName = username;
            String fullNameAttribute = configuration.getFullNameAttribute();
            if (StringUtils.isNotBlank(fullNameAttribute)) {
                fullName = ctx.getStringAttribute(fullNameAttribute);
            }
            // Email
            String email = "";
            String emailAttribute = configuration.getEmailAttribute();
            if (StringUtils.isNotBlank(emailAttribute)) {
                email = ctx.getStringAttribute(emailAttribute);
            }
            // OK
            return new PersonLDAPUserDetails(userDetails, fullName, email);
        }
    }
}
