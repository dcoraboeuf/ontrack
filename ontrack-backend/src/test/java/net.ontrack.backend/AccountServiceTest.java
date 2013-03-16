package net.ontrack.backend;

import net.ontrack.core.model.Account;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.service.AccountService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class AccountServiceTest extends AbstractValidationTest {

    @Autowired
    private AccountService accountService;

    @Test
    public void authenticate_admin() {
        Account account = accountService.authenticate("admin", "admin");
        assertNotNull(account);
        assertEquals(1, account.getId());
        assertEquals("admin", account.getName());
        assertEquals("Administrator", account.getFullName());
        assertEquals("builtin", account.getMode());
        assertEquals(SecurityRoles.ADMINISTRATOR, account.getRoleName());
    }

    @Test
    public void authenticate_wrong_name() {
        Account account = accountService.authenticate("xxx", "admin");
        assertNull(account);
    }

    @Test
    public void authenticate_wrong_password() {
        Account account = accountService.authenticate("admin", "xxx");
        assertNull(account);
    }

}
