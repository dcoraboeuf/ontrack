package net.ontrack.backend

import net.ontrack.core.security.SecurityRoles
import net.ontrack.service.AccountService
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class AccountServiceTest extends AbstractValidationTest {
	
	@Autowired
	private AccountService accountService

    @Test
    void authenticate_admin() {
        def account = accountService.authenticate('admin', 'admin');
        assert account != null
        assert 1 == account.id
        assert 'admin' == account.name
        assert 'Administrator' == account.fullName
        assert 'builtin' == account.mode
        assert SecurityRoles.ADMINISTRATOR == account.roleName
    }

    @Test
    void authenticate_wrong_name() {
        def account = accountService.authenticate('xxx', 'admin');
        assert account == null
    }

    @Test
    void authenticate_wrong_password() {
        def account = accountService.authenticate('admin', 'xxx');
        assert account == null
    }

}
