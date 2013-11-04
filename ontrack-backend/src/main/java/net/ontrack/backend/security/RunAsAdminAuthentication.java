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
    private final Account account;

    public RunAsAdminAuthentication(Account account) {
        super(AuthorityUtils.createAuthorityList(SecurityRoles.ADMINISTRATOR));
        if (account == null) {
            this.account = RUNAS_ACCOUNT;
        } else {
            this.account = new Account(
                    account.getId(),
                    account.getName(),
                    account.getFullName(),
                    "",
                    SecurityRoles.ADMINISTRATOR,
                    "runas",
                    Locale.ENGLISH
            );
        }
    }

    @Override
    public Account getDetails() {
        return account;
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
