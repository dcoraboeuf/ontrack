package net.ontrack.web.api.controller;

import net.ontrack.core.model.Account;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.web.api.model.AuthenticationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final SecurityUtils securityUtils;

    @Autowired
    public AuthenticationController(SecurityUtils securityUtils) {
        this.securityUtils = securityUtils;
    }

    /**
     * The call to this method is protected. The user must provide authentication
     * in order to access this method.
     *
     * @return
     */
    @RequestMapping(value = "/authenticate", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<AuthenticationResource> authenticate() {
        // Checks the login status
        if (securityUtils.isLogged()) {
            Account account = securityUtils.getCurrentAccount();
            return new ResponseEntity<>(
                    new AuthenticationResource(
                            account.getId(),
                            account.getName(),
                            account.getFullName()
                    ),
                    HttpStatus.ACCEPTED
            );
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

}
