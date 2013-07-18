package net.ontrack.web.hateoas;

import com.google.common.base.Function;
import net.ontrack.core.model.Account;
import net.ontrack.service.AccountService;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Controller
@RequestMapping("/rest/account")
public class AccountResourceController extends AbstractResourceController {

    private final AccountService accountService;

    @Autowired
    public AccountResourceController(ErrorHandler errorHandler, Strings strings, AccountService accountService) {
        super(errorHandler, strings);
        this.accountService = accountService;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public
    @ResponseBody
    AccountResource accountGet(@PathVariable int id) {
        return new Function<Account, AccountResource>() {

            @Override
            public AccountResource apply(Account o) {
                return new AccountResource(o).withLink(linkTo(methodOn(AccountResourceController.class).accountGet(o.getId())).withSelfRel());
            }
        }.apply(accountService.getAccount(id));
    }

}
