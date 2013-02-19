package net.ontrack.web.gui;

import net.ontrack.core.model.AccountCreationForm;
import net.ontrack.core.model.Ack;
import net.ontrack.service.AccountService;
import net.ontrack.web.support.AbstractUIController;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/ui/admin")
public class AdminUIController extends AbstractUIController {

    private final AccountService accountService;

    @Autowired
    public AdminUIController(ErrorHandler errorHandler, Strings strings, AccountService accountService) {
        super(errorHandler, strings);
        this.accountService = accountService;
    }

    /**
     * Creating an account
     */
    @RequestMapping(value = "/accounts", method = RequestMethod.POST)
    public
    @ResponseBody
    Ack createAccount(@RequestBody AccountCreationForm form) {
        return accountService.createAccount(form);
    }

}
