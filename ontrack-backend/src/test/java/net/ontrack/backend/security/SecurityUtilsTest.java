package net.ontrack.backend.security;

import net.ontrack.core.model.Account;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.core.security.SecurityUtils;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SecurityUtilsTest {

    private final SecurityUtils securityUtils = new SecurityUtilsImpl();

    @Test
    public void admin_hasRole_all () {
        withRole(SecurityRoles.ADMINISTRATOR);
        assertTrue(securityUtils.hasRole(SecurityRoles.ADMINISTRATOR));
        assertTrue(securityUtils.hasRole(SecurityRoles.CONTROLLER));
        assertTrue(securityUtils.hasRole(SecurityRoles.USER));
    }

    @Test
    public void controller_hasRole () {
        withRole(SecurityRoles.CONTROLLER);
        assertFalse(securityUtils.hasRole(SecurityRoles.ADMINISTRATOR));
        assertTrue(securityUtils.hasRole(SecurityRoles.CONTROLLER));
        assertTrue(securityUtils.hasRole(SecurityRoles.USER));
    }

    @Test
    public void user_hasRole () {
        withRole(SecurityRoles.USER);
        assertFalse(securityUtils.hasRole(SecurityRoles.ADMINISTRATOR));
        assertFalse(securityUtils.hasRole(SecurityRoles.CONTROLLER));
        assertTrue(securityUtils.hasRole(SecurityRoles.USER));
    }

    @Test
    public void xxx_hasNoRole () {
        withRole("XXX");
        assertFalse(securityUtils.hasRole(SecurityRoles.ADMINISTRATOR));
        assertFalse(securityUtils.hasRole(SecurityRoles.CONTROLLER));
        assertFalse(securityUtils.hasRole(SecurityRoles.USER));
    }

    @Test
    public void anonymous_hasNoRole () {
        SecurityContextHolder.clearContext();
        assertFalse(securityUtils.hasRole(SecurityRoles.ADMINISTRATOR));
        assertFalse(securityUtils.hasRole(SecurityRoles.CONTROLLER));
        assertFalse(securityUtils.hasRole(SecurityRoles.USER));
    }

    private void withRole(String role) {
        withAccount(new AccountAuthentication(new Account(1, "user", "User", "user@ontrack.net", role, "builtin")));
    }

    private void withAccount(AccountAuthentication authentication) {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

}
