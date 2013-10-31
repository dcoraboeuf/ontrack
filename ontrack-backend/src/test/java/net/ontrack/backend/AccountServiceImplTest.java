package net.ontrack.backend;

import net.ontrack.backend.dao.*;
import net.ontrack.backend.dao.model.TAccount;
import net.ontrack.backend.dao.model.TGlobalAuthorization;
import net.ontrack.backend.dao.model.TProjectAuthorization;
import net.ontrack.core.config.CoreConfig;
import net.ontrack.core.model.Account;
import net.ontrack.core.security.GlobalFunction;
import net.ontrack.core.security.ProjectFunction;
import net.ontrack.core.security.ProjectRole;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.service.EventService;
import net.sf.jstring.Strings;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Locale;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AccountServiceImplTest {

    private AccountServiceImpl accountService;
    private AccountDao accountDao;
    private GlobalAuthorizationDao globalAuthorizationDao;
    private ProjectAuthorizationDao projectAuthorizationDao;

    @Before
    public void before() {
        // Dependencies
        ValidatorService validatorService = mock(ValidatorService.class);
        EventService eventService = mock(EventService.class);
        Strings strings = new CoreConfig().strings();
        accountDao = mock(AccountDao.class);
        CommentDao commentDao = mock(CommentDao.class);
        ValidationRunStatusDao validationRunStatusDao = mock(ValidationRunStatusDao.class);
        EventDao eventDao = mock(EventDao.class);
        projectAuthorizationDao = mock(ProjectAuthorizationDao.class);
        globalAuthorizationDao = mock(GlobalAuthorizationDao.class);
        // Service
        accountService = new AccountServiceImpl(
                validatorService,
                eventService,
                strings,
                accountDao,
                commentDao,
                validationRunStatusDao,
                eventDao,
                projectAuthorizationDao,
                globalAuthorizationDao);
    }

    @Test
    public void getACL_admin() {
        Account account = new Account(0, "admin", "Administrator", "", SecurityRoles.ADMINISTRATOR, "builtin", Locale.ENGLISH);
        account = accountService.getACL(account);
        for (GlobalFunction fn : GlobalFunction.values()) {
            assertTrue(account.isGranted(fn));
        }
    }

    @Test
    public void getACL_controller() {
        Account account = new Account(1, "controller", "Controller", "", SecurityRoles.CONTROLLER, "builtin", Locale.ENGLISH);
        account = accountService.getACL(account);
        for (GlobalFunction fn : GlobalFunction.values()) {
            assertFalse(account.isGranted(fn));
        }
    }

    @Test
    public void getACL_user() {
        Account account = new Account(2, "user", "User", "", SecurityRoles.USER, "builtin", Locale.ENGLISH);
        account = accountService.getACL(account);
        for (GlobalFunction fn : GlobalFunction.values()) {
            assertFalse(account.isGranted(fn));
        }
    }

    @Test
    public void getACL_project_owner() {
        Account account = new Account(2, "user", "User", "", SecurityRoles.USER, "builtin", Locale.ENGLISH);
        when(projectAuthorizationDao.findByAccount(2)).thenReturn(Arrays.asList(
                new TProjectAuthorization(1, 2, ProjectRole.OWNER)
        ));
        account = accountService.getACL(account);
        for (GlobalFunction fn : GlobalFunction.values()) {
            assertFalse(account.isGranted(fn));
        }
        // Project functions for the project the user owns
        for (ProjectFunction fn : ProjectFunction.values()) {
            if (fn == ProjectFunction.PROJECT_DELETE) {
                assertFalse(fn + " cannot be granted for the project owner", account.isGranted(fn, 1));
            } else {
                assertTrue(fn + " must be granted for the project owner", account.isGranted(fn, 1));
            }
        }
        // Project functions for the project the user does not own
        for (ProjectFunction fn : ProjectFunction.values()) {
            assertFalse(fn + " cannot be granted for the project non owner", account.isGranted(fn, 2));
        }
    }

    @Test
    public void getACL_user_with_acl() {
        when(globalAuthorizationDao.findByAccount(2)).thenReturn(Arrays.asList(
                new TGlobalAuthorization(2, GlobalFunction.PROJECT_EXPORT)
        ));
        Account account = new Account(2, "user", "User", "", SecurityRoles.USER, "builtin", Locale.ENGLISH);
        account = accountService.getACL(account);
        for (GlobalFunction fn : GlobalFunction.values()) {
            if (fn == GlobalFunction.PROJECT_EXPORT) {
                assertTrue(account.isGranted(fn));
            } else {
                assertFalse(account.isGranted(fn));
            }
        }
    }

    @Test
    public void authenticate_admin() {
        TAccount t = new TAccount(0, "admin", "Administrator", "", SecurityRoles.ADMINISTRATOR, "builtin", Locale.ENGLISH);
        when(accountDao.findByNameAndPassword("admin", "admin")).thenReturn(t);
        Account account = accountService.authenticate("admin", "admin");
        for (GlobalFunction fn : GlobalFunction.values()) {
            assertTrue(account.isGranted(fn));
        }
    }

    @Test
    public void authenticate_user() {
        TAccount t = new TAccount(2, "user", "User", "", SecurityRoles.USER, "builtin", Locale.ENGLISH);
        when(accountDao.findByNameAndPassword("user", "user")).thenReturn(t);
        Account account = accountService.authenticate("user", "user");
        for (GlobalFunction fn : GlobalFunction.values()) {
            assertFalse(account.isGranted(fn));
        }
    }

}
