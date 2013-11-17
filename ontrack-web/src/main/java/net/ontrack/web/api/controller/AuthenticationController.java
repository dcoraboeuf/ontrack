package net.ontrack.web.api.controller;

import net.ontrack.core.model.Account;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.web.api.model.AuthenticationResource;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping("/api/auth")
public class AuthenticationController extends APIController {

    private final SecurityUtils securityUtils;

    @Autowired
    protected AuthenticationController(ErrorHandler errorHandler, Strings strings, SecurityUtils securityUtils) {
        super(errorHandler, strings);
        this.securityUtils = securityUtils;
    }

    /**
     * The call to this method is protected. The user must provide authentication
     * in order to access this method.
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

    /**
     * Actual logout is performed through settings in <code>web-security.xml</code>.
     */
    @RequestMapping(value = "/logged-out", method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void logout() {
    }

}
