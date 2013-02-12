package net.ontrack.web.gui;

import net.ontrack.web.support.AbstractGUIController;
import net.ontrack.web.support.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/gui/admin")
public class AdminController extends AbstractGUIController {

    @Autowired
    public AdminController(ErrorHandler errorHandler) {
        super(errorHandler);
    }

    /**
     * Settings page
     */
    @RequestMapping(value = "/settings", method = RequestMethod.GET)
    public String settings(Model model) {
        // OK
        return "settings";
    }

}
