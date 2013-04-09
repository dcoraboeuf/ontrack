package net.ontrack.web.support.fm;

import com.google.common.base.CaseFormat;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;
import net.ontrack.core.model.Account;
import net.ontrack.core.model.Entity;
import net.ontrack.core.model.EntityID;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.service.SubscriptionService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.util.*;

/**
 * Tests if the current user is subscribed to a list
 * of filters.
 */
public class FnSubscribed implements TemplateMethodModel {

    private final SecurityUtils securityUtils;
    private final SubscriptionService subscriptionService;

    public FnSubscribed(SecurityUtils securityUtils, SubscriptionService subscriptionService) {
        this.securityUtils = securityUtils;
        this.subscriptionService = subscriptionService;
    }

    @Override
    public Object exec(List list) throws TemplateModelException {
        // Checks
        Validate.notNull(list, "List of arguments is required");
        Validate.isTrue(list.size() == 1, "Filter definition is required");
        String filter = (String) list.get(0);
        // Gets the current user
        Account account = securityUtils.getCurrentAccount();
        // Subscriber?
        if (account != null
                && StringUtils.isNotBlank(account.getEmail())
                && subscriptionService.isEnabled()) {
            // Parses the filter
            Set<EntityID> entities = parseFilter(filter);
            // Checks with the subscription service
            return subscriptionService.isSubscribed(account.getId(), entities);
        } else {
            return false;
        }
    }

    private Set<EntityID> parseFilter(String filter) {
        Set<EntityID> result = new HashSet<>();
        String[] entities = StringUtils.split(filter, "&");
        for (String entityToken : entities) {
            String[] tokens = StringUtils.split(entityToken, "=");
            if (tokens.length == 2) {
                String name = tokens[0];
                int id = Integer.parseInt(tokens[1], 10);
                // Name as an entity
                String entityName = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, name);
                Entity entity = Entity.valueOf(entityName);
                // Adds to the map
                result.add(new EntityID(entity, id));
            }
        }
        return result;
    }
}
