package net.ontrack.backend.security;

import net.ontrack.core.model.Account;
import net.ontrack.core.security.SecurityRoles;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Locale;

public class RunAsAdminAuthentication extends AbstractAuthenticationToken {

    private static final Account RUNAS_ACCOUNT = new Account(
            1,
            "runas_admin",
            "Run-as-administrator",
            "",
            SecurityRoles.ADMINISTRATOR,
            "runas",
            Locale.ENGLISH
    );

    public RunAsAdminAuthentication() {
        super(AuthorityUtils.createAuthorityList(SecurityRoles.ADMINISTRATOR));
    }

    @Override
    public Account getDetails() {
        return RUNAS_ACCOUNT;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}
