package net.ontrack.web.ui;

import net.ontrack.core.model.AccountCreationForm;
import net.ontrack.core.model.AccountUpdateForm;
import net.ontrack.core.model.Ack;
import net.ontrack.core.model.Entity;
import net.ontrack.service.AccountService;
import net.ontrack.service.SubscriptionService;
import net.ontrack.web.support.AbstractUIController;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@Controller
@RequestMapping("/ui/admin")
public class AdminUIController extends AbstractUIController {

    private final AccountService accountService;
    private final SubscriptionService subscriptionService;

    @Autowired
    public AdminUIController(ErrorHandler errorHandler, Strings strings, AccountService accountService, SubscriptionService subscriptionService) {
        super(errorHandler, strings);
        this.accountService = accountService;
        this.subscriptionService = subscriptionService;
    }

    /**
     * Creating an account
     */
    @RequestMapping(value = "/accounts", method = RequestMethod.POST)
    public
    @ResponseBody
    Ack createAccount(@RequestBody AccountCreationForm form) {
        return accountService.createAccount(form).ack();
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

}
