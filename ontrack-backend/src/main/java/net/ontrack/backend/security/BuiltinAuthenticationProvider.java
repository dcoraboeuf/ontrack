package net.ontrack.backend.security;

import net.ontrack.backend.AccountService;
import net.ontrack.core.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class BuiltinAuthenticationProvider implements AuthenticationProvider {

    private final AccountService accountService;

    @Autowired
    public BuiltinAuthenticationProvider(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // User/password data
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
        // Collects authentication data
        String user = token.getName();
        String password = (String) token.getCredentials();
        // Authenticates using the account service
        Account account = accountService.authenticate(user, password);
        // If account found
        if (account != null) {
            return new AccountAuthentication(account);
        }
        // Failure, not authenticated
        else {
            return null;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
