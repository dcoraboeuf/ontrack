package net.ontrack.web.api.controller;

import net.ontrack.core.security.GlobalFunction;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.service.AccountService;
import net.ontrack.web.api.assembly.AccountAssembler;
import net.ontrack.web.api.model.AccountResource;
import net.ontrack.web.api.model.SimpleResourceCollection;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

// TODO Removes the controller name when former GUI {@link net.ontrack.web.gui.AdminController} is removed
@Controller("apiAdminController")
@RequestMapping("/api/admin")
public class AdminController extends APIController {

    private final AccountAssembler accountAssembler;
    private final AccountService accountService;
    private final SecurityUtils securityUtils;

    @Autowired
    public AdminController(ErrorHandler errorHandler, Strings strings, AccountAssembler accountAssembler, AccountService accountService, SecurityUtils securityUtils) {
        super(errorHandler, strings);
        this.accountAssembler = accountAssembler;
        this.accountService = accountService;
        this.securityUtils = securityUtils;
    }

    @RequestMapping(value = "/account", method = RequestMethod.GET)
    @ResponseBody
    public SimpleResourceCollection<AccountResource> getAccounts() {
        securityUtils.checkGrant(GlobalFunction.ACCOUNT_MANAGEMENT);
        return SimpleResourceCollection.of(
                accountService.getAccounts(),
                accountAssembler.summary())
                .withLink(linkTo(methodOn(ProjectController.class).createProject(null)).withSelfRel())
                ;
    }

}
