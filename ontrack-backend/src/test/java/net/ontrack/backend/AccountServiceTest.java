package net.ontrack.backend;

import net.ontrack.core.model.*;
import net.ontrack.core.security.*;
import net.ontrack.service.AccountService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;

import java.util.concurrent.Callable;

import static org.junit.Assert.*;

public class AccountServiceTest extends AbstractValidationTest {

    @Autowired
    private AccountService accountService;
    @Autowired
    private SecurityUtils securityUtils;

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
    public void createAccount_admin_ok() throws Exception {
        final String name = uid("A");
        Account account = asAdmin().call(new Callable<Account>() {
            @Override
            public Account call() throws Exception {
                return accountService.getAccount(
                        accountService.createAccount(
                                new AccountCreationForm(
                                        name,
                                        "Account " + name,
                                        name + "@test.com",
                                        SecurityRoles.USER,
                                        "builtin",
                                        "***",
                                        "***"
                                )
                        ).getValue());
            }
        });
        assertEquals(name, account.getName());
    }

    @Test
    public void createAccount_user_granted_ok() throws Exception {
        final String name = uid("A");
        Account account = asUser().withGlobalFn(GlobalFunction.ACCOUNT_MANAGEMENT).call(new Callable<Account>() {
            @Override
            public Account call() throws Exception {
                return accountService.getAccount(
                        accountService.createAccount(
                                new AccountCreationForm(
                                        name,
                                        "Account " + name,
                                        name + "@test.com",
                                        SecurityRoles.USER,
                                        "builtin",
                                        "***",
                                        "***"
                                )
                        ).getValue());
            }
        });
        assertEquals(name, account.getName());
    }

    @Test(expected = AccessDeniedException.class)
    public void createAccount_user_denied() throws Exception {
        final String name = uid("A");
        asUser().call(new Callable<Account>() {
            @Override
            public Account call() throws Exception {
                return accountService.getAccount(
                        accountService.createAccount(
                                new AccountCreationForm(
                                        name,
                                        "Account " + name,
                                        name + "@test.com",
                                        SecurityRoles.USER,
                                        "builtin",
                                        "***",
                                        "***"
                                )
                        ).getValue());
            }
        });
    }

    @Test
    public void change_email_ok() throws Exception {
        securityUtils.asAdmin(new Callable<Void>() {
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
        securityUtils.asAdmin(new Callable<Void>() {
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

    @Test
    public void reset_password() {
        // Creating a test account
        final ID id = securityUtils.asAdmin(new Callable<ID>() {
            @Override
            public ID call() throws Exception {
                return accountService.createAccount(new AccountCreationForm(
                        "reset_password",
                        "reset_password",
                        "reset_password@test.com",
                        SecurityRoles.USER,
                        "builtin",
                        "pwd1",
                        "pwd1"
                ));
            }
        });
        // Testing the connection
        Account account = accountService.authenticate("reset_password", "pwd1");
        assertNotNull(account);
        // Changing the password
        Ack ack = securityUtils.asAdmin(new Callable<Ack>() {
            @Override
            public Ack call() throws Exception {
                return accountService.resetPassword(id.getValue(), "pwd2");
            }
        });
        assertTrue(ack.isSuccess());
        // Old password no longer valid
        account = accountService.authenticate("reset_password", "pwd1");
        assertNull(account);
        // New password OK
        account = accountService.authenticate("reset_password", "pwd2");
        assertNotNull(account);
    }

    @Test
    public void setProjectACL_admin_ok() throws Exception {
        // Project
        final ProjectSummary project = doCreateProject();
        // Account
        final Account account = doCreateAccount();
        // Sets the ACL
        Ack ack = asAdmin().call(new Callable<Ack>() {
            @Override
            public Ack call() throws Exception {
                return accountService.setProjectACL(project.getId(), account.getId(), ProjectRole.OWNER);
            }
        });
        assertTrue("Project ACL set", ack.isSuccess());
    }

    @Test
    public void setProjectACL_owner_ok() throws Exception {
        // Project
        final ProjectSummary project = doCreateProject();
        // Account
        final Account account = doCreateAccount();
        // Sets the ACL
        Ack ack = asUser().withProjectFn(ProjectFunction.ACL, project.getId()).call(new Callable<Ack>() {
            @Override
            public Ack call() throws Exception {
                return accountService.setProjectACL(project.getId(), account.getId(), ProjectRole.OWNER);
            }
        });
        assertTrue("Project ACL set", ack.isSuccess());
    }

    @Test(expected = AccessDeniedException.class)
    public void setProjectACL_user_denied() throws Exception {
        // Project
        final ProjectSummary project = doCreateProject();
        // Account
        final Account account = doCreateAccount();
        // Sets the ACL
        asUser().call(new Callable<Ack>() {
            @Override
            public Ack call() throws Exception {
                return accountService.setProjectACL(project.getId(), account.getId(), ProjectRole.OWNER);
            }
        });
    }

}
