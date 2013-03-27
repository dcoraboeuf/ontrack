package net.ontrack.backend.security;

import com.google.common.collect.Sets;
import net.ontrack.core.model.Account;
import net.ontrack.core.security.SecurityRoles;
import org.junit.Test;
import org.springframework.security.core.authority.AuthorityUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AccountAuthenticationTest {

    @Test
    public void admin() {
        Account account = new Account(1, "admin", "Administrator", "admin@ontrack.net", "ROLE_ADMIN", "builtin");
        AccountAuthentication a = new AccountAuthentication(account);
        assertEquals(account, a.getDetails());
        assertTrue(a.isAuthenticated());
        assertEquals(
                Sets.newHashSet(SecurityRoles.ADMINISTRATOR),
                AuthorityUtils.authorityListToSet(a.getAuthorities())
        );
    }


}
