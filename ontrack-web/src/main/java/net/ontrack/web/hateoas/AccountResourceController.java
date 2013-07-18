package net.ontrack.web.hateoas;

import com.google.common.base.Function;
import net.ontrack.core.model.Account;
import net.ontrack.service.AccountService;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/rest/account")
public class AccountResourceController extends AbstractResourceController {

    private final AccountService accountService;

    @Autowired
    public AccountResourceController(Strings strings, AccountService accountService) {
        super(strings);
        this.accountService = accountService;
    }

    @RequestMapping(value = "/{id:[\\d+]+}", method = RequestMethod.GET)
    public
    @ResponseBody
    AccountResource accountGet(@PathVariable int id) {
        return new Function<Account, AccountResource>() {

            @Override
            public AccountResource apply(Account o) {
                return new AccountResource(o);
            }
        }.apply(accountService.getAccount(id));
    }

}
