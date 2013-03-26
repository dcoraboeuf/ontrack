package net.ontrack.web.gui;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import net.ontrack.core.model.UserMessage;
import net.ontrack.extension.api.configuration.ConfigurationExtension;
import net.ontrack.extension.api.configuration.ConfigurationExtensionField;
import net.ontrack.extension.api.configuration.ConfigurationExtensionService;
import net.ontrack.service.AccountService;
import net.ontrack.service.AdminService;
import net.ontrack.service.model.LDAPConfiguration;
import net.ontrack.service.model.MailConfiguration;
import net.ontrack.web.gui.model.GUIConfigurationExtension;
import net.ontrack.web.gui.model.GUIConfigurationExtensionField;
import net.ontrack.web.support.AbstractGUIController;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.LocalizableMessage;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
        // Gets the mail configuration
        model.addAttribute("mail", adminService.getMailConfiguration());
        // Gets the list of configuration extensions
        Collection<GUIConfigurationExtension> extensions = Collections2.transform(
                configurationExtensionService.getConfigurationExtensions(),
                new Function<ConfigurationExtension, GUIConfigurationExtension>() {
                    @Override
                    public GUIConfigurationExtension apply(ConfigurationExtension extension) {
                        return new GUIConfigurationExtension(
                                extension.getExtension(),
                                extension.getName(),
                                strings.get(locale, extension.getTitleKey()),
                                Lists.transform(
                                        extension.getFields(),
                                        new Function<ConfigurationExtensionField, GUIConfigurationExtensionField>() {
                                            @Override
                                            public GUIConfigurationExtensionField apply(ConfigurationExtensionField f) {
                                                return new GUIConfigurationExtensionField(
                                                        f.getName(),
                                                        strings.get(locale, f.getDisplayNameKey()),
                                                        f.getType(),
                                                        f.getDefaultValue(),
                                                        f.getValue()
                                                );
                                            }
                                        }
                                )
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
    public String ldap(LDAPConfiguration configuration, RedirectAttributes redirectAttributes) {
        // Saves the configuration
        adminService.saveLDAPConfiguration(configuration);
        // Success
        redirectAttributes.addFlashAttribute("message", UserMessage.success("ldap.saved"));
        // OK
        return "redirect:/gui/admin/settings";
    }

    /**
     * Mail settings
     */
    @RequestMapping(value = "/settings/mail", method = RequestMethod.POST)
    public String mail(MailConfiguration configuration, RedirectAttributes redirectAttributes) {
        // Saves the configuration
        adminService.saveMailConfiguration(configuration);
        // Success
        redirectAttributes.addFlashAttribute("message", UserMessage.success("mail.saved"));
        // OK
        return "redirect:/gui/admin/settings";
    }

    /**
     * Extension settings
     */
    @RequestMapping(value = "/settings/extension/{extension}/{name}", method = RequestMethod.POST)
    public String extensionSettings(
            Locale locale,
            @PathVariable String extension,
            @PathVariable String name,
            WebRequest request,
            RedirectAttributes redirectAttributes) {
        // Parameters
        Map<String, String> parameters = new HashMap<>();
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            String pName = entry.getKey();
            String[] pValues = entry.getValue();
            if (pValues != null && pValues.length == 1) {
                parameters.put(pName, pValues[0]);
            }
        }
        // Saves the configuration
        String displayNameKey = adminService.saveExtensionConfiguration(extension, name, parameters);
        // Success
        redirectAttributes.addFlashAttribute("message", UserMessage.success(strings.get(locale, "settings.extension.saved", new LocalizableMessage(displayNameKey))));
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
