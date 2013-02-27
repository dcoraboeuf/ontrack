package net.ontrack.web.gui;

import net.ontrack.web.support.AbstractGUIController;
import net.ontrack.web.support.CallbackAuthenticationSuccessHandler;
import net.ontrack.web.support.ErrorHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController extends AbstractGUIController {

    /**
     * See {@link org.springframework.security.web.savedrequest.HttpSessionRequestCache}
     */
    public static final String SPRING_SECURITY_SAVED_REQUEST = "SPRING_SECURITY_SAVED_REQUEST";

    @Autowired
    public LoginController(ErrorHandler errorHandler) {
        super(errorHandler);
    }

    @RequestMapping("/login")
    public String login(@RequestParam(required = false) String callbackUrl, Model model) {
        if (StringUtils.isNotBlank(callbackUrl)) {
            model.addAttribute(CallbackAuthenticationSuccessHandler.CALLBACK_URL, callbackUrl);
        }
        return "login";
    }

}
