package net.ontrack.web.ui;

import net.ontrack.core.model.Ack;
import net.ontrack.web.support.AbstractUIController;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginUIController extends AbstractUIController {

    @Autowired
    public LoginUIController(ErrorHandler errorHandler, Strings strings) {
        super(errorHandler, strings);
    }

    /**
     * Forces the login of the user for pure-UI calls.
     */
    @RequestMapping(value = "/ui/login", method = RequestMethod.GET)
    public
    @ResponseBody
    Ack login() {
        return Ack.OK;
    }

}
