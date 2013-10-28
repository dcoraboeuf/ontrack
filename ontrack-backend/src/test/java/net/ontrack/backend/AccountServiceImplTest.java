package net.ontrack.backend;

import net.ontrack.backend.dao.AccountDao;
import net.ontrack.backend.dao.CommentDao;
import net.ontrack.backend.dao.EventDao;
import net.ontrack.backend.dao.ValidationRunStatusDao;
import net.ontrack.core.config.CoreConfig;
import net.ontrack.core.model.Account;
import net.ontrack.core.security.GlobalFunction;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.service.EventService;
import net.sf.jstring.Strings;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class AccountServiceImplTest {

    private AccountServiceImpl accountService;

    @Before
    public void before() {
        // Dependencies
        ValidatorService validatorService = mock(ValidatorService.class);
        EventService eventService = mock(EventService.class);
        Strings strings = new CoreConfig().strings();
        AccountDao accountDao = mock(AccountDao.class);
        CommentDao commentDao = mock(CommentDao.class);
        ValidationRunStatusDao validationRunStatusDao = mock(ValidationRunStatusDao.class);
        EventDao eventDao = mock(EventDao.class);
        // Service
        accountService = new AccountServiceImpl(
                validatorService,
                eventService,
                strings,
                accountDao,
                commentDao,
                validationRunStatusDao,
                eventDao
        );
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

}
