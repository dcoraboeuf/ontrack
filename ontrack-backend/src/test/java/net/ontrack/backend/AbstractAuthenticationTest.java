package net.ontrack.backend;

import net.ontrack.backend.security.AccountAuthentication;
import net.ontrack.core.model.Account;
import net.ontrack.core.security.SecurityRoles;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import java.util.concurrent.Callable;

public abstract class AbstractAuthenticationTest extends AbstractValidationTest {

    protected <T> T asAdmin(Callable<T> call) throws Exception {
        SecurityContext old = SecurityContextHolder.getContext();
        try {
            SecurityContext securityContext = new SecurityContextImpl();
            Authentication authentication = new AccountAuthentication(
                    new Account(
                            0,
                            "admin",
                            "Administrator",
                            "admin@test.com",
                            SecurityRoles.ADMINISTRATOR,
                            "builtin"
                    )
            );
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);
            return call.call();
        } finally {
            SecurityContextHolder.setContext(old);
        }
    }

}
