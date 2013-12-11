package net.ontrack.web.api.controller;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import net.ontrack.core.model.Account;
import net.ontrack.core.security.AuthorizationPolicy;
import net.ontrack.core.security.AuthorizationUtils;
import net.ontrack.core.security.GlobalFunction;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.extension.api.ExtensionManager;
import net.ontrack.extension.api.action.TopActionExtension;
import net.ontrack.web.api.model.ActionResource;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Controller
@RequestMapping("/api/auth")
public class AuthenticationController extends APIController {

    private final SecurityUtils securityUtils;
    private final ExtensionManager extensionManager;
    private final AuthorizationUtils authorizationUtils;

    @Autowired
    protected AuthenticationController(ErrorHandler errorHandler, Strings strings, SecurityUtils securityUtils, ExtensionManager extensionManager, AuthorizationUtils authorizationUtils) {
        super(errorHandler, strings);
        this.securityUtils = securityUtils;
        this.extensionManager = extensionManager;
        this.authorizationUtils = authorizationUtils;
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
            // List of available actions
            List<ActionResource> actions = new ArrayList<>();
            // Built-in actions
            fillBuiltinActions(actions);
            // Extension actions
            Collection<? extends TopActionExtension> topLevelActions = extensionManager.getTopLevelActions();
            // Filter on access rights
            topLevelActions = Collections2.filter(
                    topLevelActions,
                    new Predicate<TopActionExtension>() {
                        @Override
                        public boolean apply(TopActionExtension action) {
                            AuthorizationPolicy policy = action.getAuthorizationPolicy();
                            return authorizationUtils.applyPolicy(policy);
                        }
                    }
            );
            // Adds them to the list of actions
            for (TopActionExtension action : topLevelActions) {
                actions.add(new ActionResource(action.getTitleKey()));
                // TODO Action view
            }
            // Response
            return new ResponseEntity<>(
                    new AuthenticationResource(
                            account.getId(),
                            account.getName(),
                            account.getFullName(),
                            actions
                    ),
                    HttpStatus.ACCEPTED
            );
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    protected void fillBuiltinActions(List<ActionResource> actions) {
        // Account management
        addAction(actions, "accounts", GlobalFunction.ACCOUNT_MANAGEMENT)
                .withView("admin/account")
        ;
        // Settings
        addAction(actions, "settings", GlobalFunction.SETTINGS)
                .withView("admin/settings")
        ;
        // Extensions
        addAction(actions, "extensions", GlobalFunction.EXTENSIONS)
                .withView("admin/extensions")
        ;
        // Subscriptions
        addAction(actions, "admin.subscriptions", GlobalFunction.SUBSCRIPTIONS_MANAGEMENT)
                .withView("admin/subscriptions")
        ;
        // Profile
        actions.add(new ActionResource("profile")
                .withView("profile")
        );
    }

    protected ActionResource addAction(List<ActionResource> actions, String key, GlobalFunction fn) {
        if (securityUtils.isGranted(fn)) {
            ActionResource actionResource = new ActionResource(key);
            actions.add(actionResource);
            return actionResource;
        } else {
            return new ActionResource("");
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
