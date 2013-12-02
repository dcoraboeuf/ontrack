package net.ontrack.web.gui;

import net.ontrack.web.support.AbstractGUIController;
import net.ontrack.web.support.CallbackAuthenticationSuccessHandler;
import net.ontrack.web.support.ErrorHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController extends AbstractGUIController {

    @Autowired
    public LoginController(ErrorHandler errorHandler) {
        super(errorHandler);
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(@RequestParam(required = false) String callbackUrl, Model model) {
        if (StringUtils.isNotBlank(callbackUrl)) {
            model.addAttribute(CallbackAuthenticationSuccessHandler.CALLBACK_URL, callbackUrl);
        }
        return "login";
    }

}
