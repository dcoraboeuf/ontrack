package net.ontrack.acceptance.client;

import net.ontrack.client.AdminUIClient;
import net.ontrack.client.support.AdminClientCall;
import net.ontrack.client.support.ClientForbiddenException;
import net.ontrack.core.model.Account;
import net.ontrack.core.model.AccountCreationForm;
import net.ontrack.core.security.SecurityRoles;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ITAdmin extends AbstractEnv {

    @Test
    public void admin_create_user() {
        Account account = doCreateUser(
                "ui_admin_01",
                "UI admin 01",
                "ui-admin-01@test.com",
                SecurityRoles.USER,
                "builtin",
                "pwd"
        );
        assertNotNull(account);
        assertEquals("ui_admin_01", account.getName());
        assertEquals("UI admin 01", account.getFullName());
        assertEquals("ui-admin-01@test.com", account.getEmail());
        assertEquals(SecurityRoles.USER, account.getRoleName());
        assertEquals("builtin", account.getMode());
    }

    @Test(expected = ClientForbiddenException.class)
    public void user_cannot_create_user() {
        // Prerequisistes
        Account account = doCreateUser();
        // Using this account, tries to create a user
        getClient().asUser(account.getName(), "test", new AdminClientCall<Void>() {
            @Override
            public Void onCall(AdminUIClient ui) {
                ui.createAccount(new AccountCreationForm(
                        "ui_admin_02",
                        "UI admin 02",
                        "ui-admin-02@test.com",
                        SecurityRoles.USER,
                        "builtin",
                        "pwd",
                        "pwd"
                ));
                return null;
            }
        });
    }

    @Test
    public void admin_create_user_with_same_name() {
        // Prerequisites
        final Account account = doCreateUser();
        final String accountName = account.getName();
        // Creates the same user
        assertClientMessage(
                new Runnable() {

                    @Override
                    public void run() {
                        asAdmin(new AdminClientCall<Void>() {
                            @Override
                            public Void onCall(AdminUIClient ui) {
                                ui.createAccount(new AccountCreationForm(
                                        accountName,
                                        account.getFullName(),
                                        account.getEmail(),
                                        account.getRoleName(),
                                        account.getMode(),
                                        "test",
                                        "test"
                                ));
                                return null;
                            }
                        });
                    }
                },
                "Account with name \"%s\" already exists.", accountName);
    }

}
