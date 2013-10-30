package net.ontrack.web.ui;

import net.ontrack.core.model.*;
import net.ontrack.core.security.GlobalFunction;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.core.ui.AdminUI;
import net.ontrack.extension.api.ExtensionManager;
import net.ontrack.service.AccountService;
import net.ontrack.service.ProfileService;
import net.ontrack.service.SubscriptionService;
import net.ontrack.web.support.AbstractUIController;
import net.ontrack.web.support.EntityConverter;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

@Controller
@RequestMapping("/ui/admin")
public class AdminUIController extends AbstractUIController implements AdminUI {

    private final AccountService accountService;
    private final SubscriptionService subscriptionService;
    private final ProfileService profileService;
    private final ExtensionManager extensionManager;
    private final EntityConverter entityConverter;
    private final SecurityUtils securityUtils;

    @Autowired
    public AdminUIController(ErrorHandler errorHandler, Strings strings, AccountService accountService, SubscriptionService subscriptionService, ProfileService profileService, ExtensionManager extensionManager, EntityConverter entityConverter, SecurityUtils securityUtils) {
        super(errorHandler, strings);
        this.accountService = accountService;
        this.subscriptionService = subscriptionService;
        this.profileService = profileService;
        this.extensionManager = extensionManager;
        this.entityConverter = entityConverter;
        this.securityUtils = securityUtils;
    }

    @Override
    @RequestMapping(value = "/acl/global", method = RequestMethod.GET)
    public
    @ResponseBody
    List<GlobalFunction> getGlobalFunctions() {
        return Arrays.asList(GlobalFunction.values());
    }

    @Override
    @RequestMapping(value = "/acl/global/{account:\\d+}/{fn:[A-Z_]+}", method = RequestMethod.PUT)
    public
    @ResponseBody
    Ack setGlobalACL(@PathVariable int account, @PathVariable GlobalFunction fn) {
        return accountService.setGlobalACL(account, fn);
    }

    /**
     * Changes the language for the current account
     */
    @RequestMapping(value = "/profile/language/{lang:[a-z_]+}", method = RequestMethod.PUT)
    public
    @ResponseBody
    Ack changeProfileLanguage(@PathVariable final String lang) {
        final Account currentAccount = securityUtils.getCurrentAccount();
        if (currentAccount != null) {
            return securityUtils.asAdmin(new Callable<Ack>() {
                @Override
                public Ack call() throws Exception {
                    Ack ack = accountService.changeLanguage(currentAccount.getId(), lang);
                    if (ack.isSuccess()) {
                        currentAccount.setLocale(
                                // Making sure to get the locale that has actually been
                                // saved after having been filtered
                                accountService.getAccount(currentAccount.getId()).getLocale()
                        );
                    }
                    return ack;
                }
            });
        } else {
            return Ack.NOK;
        }
    }

    /**
     * List of accounts
     */
    @Override
    @RequestMapping(value = "/accounts", method = RequestMethod.GET)
    public
    @ResponseBody
    List<Account> accounts() {
        return accountService.getAccounts();
    }

    /**
     * Getting an account
     */
    @Override
    @RequestMapping(value = "/accounts/{id:\\d+}", method = RequestMethod.GET)
    public
    @ResponseBody
    Account account(@PathVariable int id) {
        return accountService.getAccount(id);
    }

    /**
     * Deleting an account
     */
    @Override
    @RequestMapping(value = "/accounts/{id:\\d+}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    Ack deleteAccount(@PathVariable int id) {
        accountService.deleteAccount(id);
        return Ack.OK;
    }

    /**
     * Creating an account
     */
    @Override
    @RequestMapping(value = "/accounts", method = RequestMethod.POST)
    public
    @ResponseBody
    ID createAccount(@RequestBody AccountCreationForm form) {
        return accountService.createAccount(form);
    }

    /**
     * Actual update of an account
     */
    @RequestMapping(value = "/accounts/{id:\\d+}/update", method = RequestMethod.PUT)
    public
    @ResponseBody
    Ack accountUpdate(@PathVariable int id, @RequestBody AccountUpdateForm form) {
        accountService.updateAccount(id, form);
        return Ack.OK;
    }

    /**
     * Password reset
     */
    @RequestMapping(value = "/accounts/{id:\\d+}/passwordReset", method = RequestMethod.PUT)
    public
    @ResponseBody
    Ack accountPasswordReset(@PathVariable int id, @RequestBody AccountPasswordResetForm form) {
        return accountService.resetPassword(
                id,
                form.getPassword());
    }

    /**
     * Deletion of a subscription
     */
    @RequestMapping(value = "/subscriptions/{entity}/{id:\\d+}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    Ack subscriptionDelete(@PathVariable Entity entity, @PathVariable int id) {
        return subscriptionService.unsubscribe(Collections.singletonMap(entity, id));
    }

    /**
     * Deletion of a subscription
     */
    @RequestMapping(value = "/subscriptions/{user:\\d+}/{entity}/{id:\\d+}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    Ack subscriptionDeleteForUser(@PathVariable int user, @PathVariable Entity entity, @PathVariable int id) {
        return subscriptionService.unsubscribeUser(user, Collections.singletonMap(entity, id));
    }

    /**
     * Filtering of validation stamps - remove from filters
     */
    @RequestMapping(value = "/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/validation_stamp/{validationStamp:[A-Za-z0-9_\\.\\-]+}/filter", method = RequestMethod.DELETE)
    public
    @ResponseBody
    Ack removeFilterValidationStamp(@PathVariable String project, @PathVariable String branch, @PathVariable String validationStamp) {
        return profileService.removeFilterValidationStamp(entityConverter.getValidationStampId(project, branch, validationStamp));
    }

    /**
     * Filtering of validation stamps - adding to filters
     */
    @RequestMapping(value = "/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/validation_stamp/{validationStamp:[A-Za-z0-9_\\.\\-]+}/filter", method = RequestMethod.PUT)
    public
    @ResponseBody
    Ack addFilterValidationStamp(@PathVariable String project, @PathVariable String branch, @PathVariable String validationStamp) {
        return profileService.addFilterValidationStamp(entityConverter.getValidationStampId(project, branch, validationStamp));
    }

    /**
     * Saving build filters
     */
    @RequestMapping(value = "/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/filter", method = RequestMethod.PUT)
    public
    @ResponseBody
    Ack saveFilter(@PathVariable String project, @PathVariable String branch, @RequestBody BuildFilter buildFilter) {
        return profileService.saveFilter(entityConverter.getBranchId(project, branch), buildFilter);
    }

    /**
     * Deleting a build filter
     */
    @RequestMapping(value = "/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/filter/{name:.*}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    Ack saveFilter(@PathVariable String project, @PathVariable String branch, @PathVariable String name) {
        return profileService.deleteFilter(entityConverter.getBranchId(project, branch), name);
    }

    /**
     * Administration of the extensions
     */
    @RequestMapping(value = "/extensions", method = RequestMethod.GET)
    public
    @ResponseBody
    List<ExtensionSummary> extensions(Locale locale) {
        return extensionManager.getExtensionTree(locale);
    }

    /**
     * Enabling an extension
     */
    @Override
    @RequestMapping(value = "/extensions/{name}", method = RequestMethod.PUT)
    public
    @ResponseBody
    Ack enableExtension(@PathVariable String name) {
        return extensionManager.enableExtension(name);
    }

    /**
     * Disabling an extension
     */
    @Override
    @RequestMapping(value = "/extensions/{name}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    Ack disableExtension(@PathVariable String name) {
        return extensionManager.disableExtension(name);
    }

    @Override
    @RequestMapping(value = "/account/lookup/{query:[a-zA-Z0-9\\-_]*}", method = RequestMethod.GET)
    public
    @ResponseBody
    List<AccountSummary> accountLookup(@PathVariable String query) {
        return accountService.accountLookup(query);
    }


}
