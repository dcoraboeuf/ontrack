package net.ontrack.acceptance.client;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import net.ontrack.client.AdminUIClient;
import net.ontrack.client.support.AdminClientCall;
import net.ontrack.core.model.Account;
import net.ontrack.core.model.AccountCreationForm;
import net.ontrack.core.model.ID;
import net.ontrack.core.security.SecurityRoles;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ITAdmin extends AbstractEnv {

    @Test
    public void admin_create_user() {
        getClient().asUser("admin", "admin", new AdminClientCall<Void>() {
            @Override
            public Void onCall(AdminUIClient ui) {
                final String accountName = "ui_admin_01";
                // Finds the account with the same name
                Account account = Iterables.find(
                        ui.accounts(),
                        new Predicate<Account>() {
                            @Override
                            public boolean apply(Account a) {
                                return StringUtils.equals(accountName, a.getName());
                            }
                        },
                        null
                );
                // Deletes it if it exists
                if (account != null) {
                    ui.deleteAccount(account.getId());
                }
                // Creates the account
                ID id = ui.createAccount(new AccountCreationForm(
                        accountName,
                        "UI admin 01",
                        "ui-admin-01@test.com",
                        SecurityRoles.USER,
                        "builtin",
                        "pwd",
                        "pwd"
                ));
                // Checks the account has been created
                account = ui.account(id.getValue());
                assertNotNull(account);
                assertEquals(id.getValue(), account.getId());
                assertEquals(accountName, account.getName());
                assertEquals("UI admin 01", account.getFullName());
                assertEquals("ui-admin-01@test.com", account.getEmail());
                assertEquals(SecurityRoles.USER, account.getRoleName());
                assertEquals("builtin", account.getMode());
                // OK
                return null;
            }
        });
    }

    @Test
    public void user_cannot_create_user() {

    }

}
