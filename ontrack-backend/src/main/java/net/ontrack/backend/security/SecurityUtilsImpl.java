package net.ontrack.backend.security;

import net.ontrack.core.model.Account;
import net.ontrack.core.model.Signature;
import net.ontrack.core.security.GlobalFunction;
import net.ontrack.core.security.ProjectFunction;
import net.ontrack.core.security.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Service;

import java.util.concurrent.Callable;

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
    public void checkIsLogged() {
        if (!isLogged()) {
            throw new AccessDeniedException("Authentication is required");
        }
    }

    @Override
    public <T> T asAdmin(Callable<T> call) {
        try {
            return asAdminTask(call).call();
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception e) {
            throw new AsAdminCallException(e);
        }
    }

    @Override
    public <T> Callable<T> asAdminTask(Callable<T> callable) {
        // Gets the current account (if any)
        Account account = getCurrentAccount();
        // Creates a temporary admin context
        SecurityContextImpl adminContext = new SecurityContextImpl();
        adminContext.setAuthentication(new RunAsAdminAuthentication(account));
        // Returns a callable that sets the context before running the target callable
        return withSecurityContext(callable, adminContext);
    }

    protected <T> Callable<T> withSecurityContext(final Callable<T> callable, final SecurityContext context) {
        // Returns a callable that sets the context before running the target callable
        return new Callable<T>() {
            @Override
            public T call() throws Exception {
                SecurityContextHolder.setContext(context);
                try {
                    // Result
                    return callable.call();
                } finally {
                    SecurityContextHolder.clearContext();
                }
            }
        };
    }

    @Override
    public <T> Callable<T> withCurrentCredentials(Callable<T> callable) {
        // Current context
        SecurityContext context = SecurityContextHolder.getContext();
        // Uses it
        return withSecurityContext(callable, context);
    }

    @Override
    public void checkGrant(GlobalFunction fn) {
        if (!isGranted(fn)) {
            throw new AccessDeniedException("Grant to " + fn + " is required");
        }
    }

    @Override
    public void checkGrant(ProjectFunction fn, int project) {
        if (!isGranted(fn, project)) {
            throw new AccessDeniedException("Grant to " + fn + " is required for project " + project);
        }
    }

    @Override
    public boolean isGranted(GlobalFunction fn) {
        Account account = getCurrentAccount();
        return account != null && account.isGranted(fn);
    }

    @Override
    public boolean isGranted(ProjectFunction fn, int project) {
        Account account = getCurrentAccount();
        return account != null && account.isGranted(fn, project);
    }
}
