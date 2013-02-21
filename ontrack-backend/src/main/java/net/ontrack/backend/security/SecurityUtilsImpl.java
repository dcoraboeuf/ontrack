package net.ontrack.backend.security;

import net.ontrack.core.model.Account;
import net.ontrack.core.model.Signature;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.core.security.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityUtilsImpl implements SecurityUtils {

    @Override
    public boolean isLogged() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        return (authentication != null && authentication.isAuthenticated() && (authentication.getDetails() instanceof Account));
    }

    @Override
    public Account getCurrentAccount() {
        if (isLogged()) {
            return (Account) SecurityContextHolder.getContext().getAuthentication().getDetails();
        } else {
            return null;
        }
    }

    @Override
    public int getCurrentAccountId() {
        Account account = getCurrentAccount();
        return account != null ? account.getId() : -1;
    }

    @Override
    public Signature getCurrentSignature() {
        Account account = getCurrentAccount();
        String accountName;
        if (account != null) {
            accountName = account.getFullName();
            if (StringUtils.isBlank(accountName)) {
                accountName = account.getName();
            }
        } else {
            accountName = "Anonymous";
        }
        // Account ID = 0 means that the account was generated on the fly (probably though LDAP connection)
        // without any actual Account row being there
        Integer accountId = account != null ? (account.getId() != 0 ? account.getId() : null) : null;
        return new Signature(accountId, accountName);
    }

    @Override
    public boolean isAdmin() {
        Account account = getCurrentAccount();
        return account != null && SecurityRoles.ADMINISTRATOR.equals(account.getRoleName());
    }
}
