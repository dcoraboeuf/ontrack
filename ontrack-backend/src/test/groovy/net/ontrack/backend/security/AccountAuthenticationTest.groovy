package net.ontrack.backend.security

import net.ontrack.core.model.Account
import net.ontrack.core.security.SecurityRoles
import org.junit.Test

class AccountAuthenticationTest {

    @Test
    void admin () {
        Account account = new Account(1, 'admin', 'Administrator', 'ROLE_ADMIN', 'builtin')
        AccountAuthentication a = new AccountAuthentication(account)
        assert account == a.details
        assert a.authenticated
        assert [ SecurityRoles.ADMINISTRATOR ] == a.authorities*.authority
    }

}
