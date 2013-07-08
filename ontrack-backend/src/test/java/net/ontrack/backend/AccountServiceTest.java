package net.ontrack.backend;

import net.ontrack.core.model.*;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.service.AccountService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.Callable;

import static org.junit.Assert.*;

public class AccountServiceTest extends AbstractAuthenticationTest {

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

    @Test
    public void change_email_ok() throws Exception {
        asAdmin(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                ID id = accountService.createAccount(new AccountCreationForm(
                        "change_email_ok",
                        "change_email_ok",
                        "change_email_ok@test.com",
                        SecurityRoles.USER,
                        "builtin",
                        "mypwd",
                        "mypwd"
                ));
                Ack ack = accountService.changeEmail(
                        id.getValue(),
                        new EmailChangeForm(
                                "mypwd",
                                "newemail@test.com"
                        )
                );
                assertTrue(ack.isSuccess());
                Account a = accountService.getAccount(id.getValue());
                assertEquals("newemail@test.com", a.getEmail());
                return null;
            }
        });
    }

    @Test
    public void change_email_ko() throws Exception {
        asAdmin(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                ID id = accountService.createAccount(new AccountCreationForm(
                        "change_email_ko",
                        "change_email_ko",
                        "change_email_ko@test.com",
                        SecurityRoles.USER,
                        "builtin",
                        "mypwd",
                        "mypwd"
                ));
                Ack ack = accountService.changeEmail(
                        id.getValue(),
                        new EmailChangeForm(
                                "mypwd2",
                                "newemail@test.com"
                        )
                );
                assertFalse(ack.isSuccess());
                Account a = accountService.getAccount(id.getValue());
                assertEquals("change_email_ko@test.com", a.getEmail());
                return null;
            }
        });
    }

}
