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
    public void granted_global_admin() {
        Account account = new Account(0, "admin", "Administrator", "", SecurityRoles.ADMINISTRATOR, "builtin", Locale.ENGLISH);
        for (GlobalFunction fn : GlobalFunction.values()) {
            assertTrue("Admin account must be granted with " + fn, account.isGranted(fn));
        }
        for (ProjectFunction fn : ProjectFunction.values()) {
            assertTrue("Admin account must be granted with " + fn + " for project 1", account.isGranted(fn, 1));
        }
    }

    @Test
    public void granted_global_controller() {
        Account account = new Account(0, "controller", "Controller", "", SecurityRoles.CONTROLLER, "builtin", Locale.ENGLISH);
        for (GlobalFunction fn : GlobalFunction.values()) {
            assertFalse("Controller account must be granted with " + fn, account.isGranted(fn));
        }
        for (ProjectFunction fn : ProjectFunction.values()) {
            if (fn == ProjectFunction.BUILD_CREATE || fn == ProjectFunction.VALIDATION_RUN_CREATE || fn == ProjectFunction.PROMOTED_RUN_CREATE) {
                assertTrue("Controller account must be granted with " + fn + " for project 1", account.isGranted(fn, 1));
            } else {
                assertFalse("Controller account must not be granted with " + fn + " for project 1", account.isGranted(fn, 1));
            }
        }
    }

    @Test
    public void granted_global_user_no_acl() {
        Account account = testAccount();
        for (GlobalFunction fn : GlobalFunction.values()) {
            assertFalse("User account must not be granted with " + fn, account.isGranted(fn));
        }
        for (ProjectFunction fn : ProjectFunction.values()) {
            assertFalse("User account must not be granted with " + fn + " for project 1", account.isGranted(fn, 1));
        }
    }

    @Test
    public void granted_global_user_with_acl() {
        Account account = testAccount().withProjectACL(ProjectFunction.BUILD_CLEANUP_CONFIG, 1);
        for (GlobalFunction fn : GlobalFunction.values()) {
            assertFalse("User account must not be granted with " + fn, account.isGranted(fn));
        }
        for (ProjectFunction fn : ProjectFunction.values()) {
            if (fn == ProjectFunction.BUILD_CLEANUP_CONFIG) {
                assertTrue("User account must be granted with " + fn + " for project 1", account.isGranted(fn, 1));
            } else {
                assertFalse("User account must not be granted with " + fn + " for project 1", account.isGranted(fn, 1));
            }
        }
    }

    private Account testAccount() {
        return new Account(1, "test", "Test user", "test@test.com", SecurityRoles.USER, "builtin", Locale.ENGLISH);
    }

    @Test
    public void acl_empty() {
        Account account = testAccount();
        for (GlobalFunction fn : GlobalFunction.values()) {
            assertFalse(account.isGranted(fn));
        }
        for (ProjectFunction fn : ProjectFunction.values()) {
            assertFalse(account.isGranted(fn, 1));
        }
    }

    @Test
    public void acl_global() {
        Account account = testAccount();
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
        Account account = testAccount();
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
