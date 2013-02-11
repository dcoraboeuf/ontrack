package net.ontrack.web.gui;

import net.ontrack.web.support.AbstractGUIController;
import net.ontrack.web.support.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController extends AbstractGUIController {

    @Autowired
    public LoginController(ErrorHandler errorHandler) {
        super(errorHandler);
    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

}
