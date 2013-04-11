package net.ontrack.web.support.fm;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;
import net.ontrack.core.model.Account;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.service.SubscriptionService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.util.List;

public class FnSecSubscriber implements TemplateMethodModel {

    private final SecurityUtils securityUtils;
    private final SubscriptionService subscriptionService;

    public FnSecSubscriber(SecurityUtils securityUtils, SubscriptionService subscriptionService) {
        this.securityUtils = securityUtils;
        this.subscriptionService = subscriptionService;
    }

    @Override
    public Object exec(List list) throws TemplateModelException {
        // Checks
        Validate.notNull(list, "List of arguments is required");
        Validate.isTrue(list.size() == 0, "No argument is needed");
        // Gets the current user
        Account account = securityUtils.getCurrentAccount();
        // Test
        return account != null
                && account.getId() != 0
                && StringUtils.isNotBlank(account.getEmail())
                && subscriptionService.isEnabled();
    }
}
