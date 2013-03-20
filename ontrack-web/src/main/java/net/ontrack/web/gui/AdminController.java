package net.ontrack.web.gui;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import net.ontrack.core.model.UserMessage;
import net.ontrack.extension.api.configuration.ConfigurationExtension;
import net.ontrack.extension.api.configuration.ConfigurationExtensionService;
import net.ontrack.service.AccountService;
import net.ontrack.service.AdminService;
import net.ontrack.service.model.LDAPConfiguration;
import net.ontrack.web.gui.model.GUIConfigurationExtension;
import net.ontrack.web.support.AbstractGUIController;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collection;
import java.util.Locale;

@Controller
@RequestMapping("/gui/admin")
public class AdminController extends AbstractGUIController {

    private final AdminService adminService;
    private final AccountService accountService;
    private final ConfigurationExtensionService configurationExtensionService;
    private final Strings strings;

    @Autowired
    public AdminController(
            ErrorHandler errorHandler,
            AdminService adminService,
            AccountService accountService,
            ConfigurationExtensionService configurationExtensionService,
            Strings strings) {
        super(errorHandler);
        this.adminService = adminService;
        this.accountService = accountService;
        this.configurationExtensionService = configurationExtensionService;
        this.strings = strings;
    }

    /**
     * Settings page
     */
    @RequestMapping(value = "/settings", method = RequestMethod.GET)
    public String settings(final Locale locale,
                           Model model) {
        // Gets the LDAP configuration
        LDAPConfiguration configuration = adminService.getLDAPConfiguration();
        model.addAttribute("ldap", configuration);
        // Gets the list of configuration extensions
        Collection<GUIConfigurationExtension> extensions = Collections2.transform(
                configurationExtensionService.getConfigurationExtensions(),
                new Function<ConfigurationExtension, GUIConfigurationExtension>() {
                    @Override
                    public GUIConfigurationExtension apply(ConfigurationExtension extension) {
                        return new GUIConfigurationExtension(
                                extension.getExtension(),
                                extension.getName(),
                                strings.get(locale, extension.getTitleKey())
                        );
                    }
                }
        );
        model.addAttribute("extensions", extensions);
        // OK
        return "settings";
    }

    /**
     * LDAP settings
     */
    @RequestMapping(value = "/settings/ldap", method = RequestMethod.POST)
    public String ldap(Locale locale, Model model, LDAPConfiguration configuration, RedirectAttributes redirectAttributes) {
        // Saves the configuration
        adminService.saveLDAPConfiguration(configuration);
        // Success
        redirectAttributes.addFlashAttribute("message", UserMessage.success(strings.get(locale, "ldap.saved")));
        // OK
        return "redirect:/gui/admin/settings";
    }

    /**
     * Management of accounts
     */
    @RequestMapping(value = "/accounts", method = RequestMethod.GET)
    public String accounts(Model model) {
        model.addAttribute("accounts", accountService.getAccounts());
        return "accounts";
    }

}
