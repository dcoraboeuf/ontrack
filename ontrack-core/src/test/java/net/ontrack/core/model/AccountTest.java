package net.ontrack.core.model;

import net.ontrack.core.security.GlobalFunction;
import net.ontrack.core.security.ProjectFunction;
import net.ontrack.core.security.SecurityRoles;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AccountTest {

    @Test
    public void acl_empty() {
        Account account = new Account(1, "test", "Test user", "test@test.com", SecurityRoles.USER, "builtin", Locale.ENGLISH);
        for (GlobalFunction fn : GlobalFunction.values()) {
            assertFalse(account.isGranted(fn));
        }
        for (ProjectFunction fn : ProjectFunction.values()) {
            assertFalse(account.isGranted(fn, 1));
        }
    }

    @Test
    public void acl_global() {
        Account account = new Account(1, "test", "Test user", "test@test.com", SecurityRoles.USER, "builtin", Locale.ENGLISH);
        account = account.withGlobalACL(GlobalFunction.SUBSCRIPTIONS_MANAGEMENT);
        for (GlobalFunction fn : GlobalFunction.values()) {
            if (fn == GlobalFunction.SUBSCRIPTIONS_MANAGEMENT) {
                assertTrue(account.isGranted(fn));
            } else {
                assertFalse(account.isGranted(fn));
            }
        }
        for (ProjectFunction fn : ProjectFunction.values()) {
            assertFalse(account.isGranted(fn, 1));
        }
    }

    @Test
    public void acl_project() {
        Account account = new Account(1, "test", "Test user", "test@test.com", SecurityRoles.USER, "builtin", Locale.ENGLISH);
        account = account.withProjectACL(ProjectFunction.BUILD_CLEANUP_CONFIG, 1);
        for (GlobalFunction fn : GlobalFunction.values()) {
            assertFalse(account.isGranted(fn));
        }
        for (int i = 1; i < 3; i++) {
            for (ProjectFunction fn : ProjectFunction.values()) {
                if (fn == ProjectFunction.BUILD_CLEANUP_CONFIG && i == 1) {
                    assertTrue(account.isGranted(fn, i));
                } else {
                    assertFalse(account.isGranted(fn, i));
                }
            }
        }
    }

}
