package net.ontrack.web.gui;

import net.ontrack.service.AdminService;
import net.ontrack.service.model.LDAPConfiguration;
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

    private final AdminService adminService;

    @Autowired
    public AdminController(ErrorHandler errorHandler, AdminService adminService) {
        super(errorHandler);
        this.adminService = adminService;
    }

    /**
     * Settings page
     */
    @RequestMapping(value = "/settings", method = RequestMethod.GET)
    public String settings(Model model) {
        // Gets the LDAP configuration
        LDAPConfiguration configuration = adminService.getLDAPConfiguration();
        model.addAttribute("ldap", configuration);
        // OK
        return "settings";
    }

    /**
     * LDAP settings
     */
    @RequestMapping(value = "/settings/ldap", method = RequestMethod.POST)
    public String ldap(Model model, LDAPConfiguration configuration) {
        // FIXME Saves the configuration
        // OK
        // TODO Uses flash attribute for the success message
        return "redirect:/gui/admin/settings";
    }

}
