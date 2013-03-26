package net.ontrack.backend;

import net.ontrack.backend.dao.SubscriptionDao;
import net.ontrack.core.model.Ack;
import net.ontrack.core.model.Entity;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.service.SubscriptionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class DefaultSubscriptionService implements SubscriptionService {

    private final SecurityUtils securityUtils;
    private final ConfigurationService configurationService;
    private final SubscriptionDao subscriptionDao;

    @Autowired
    public DefaultSubscriptionService(SecurityUtils securityUtils, ConfigurationService configurationService, SubscriptionDao subscriptionDao) {
        this.securityUtils = securityUtils;
        this.configurationService = configurationService;
        this.subscriptionDao = subscriptionDao;
    }

    @Override
    public boolean isEnabled() {
        return StringUtils.isNotBlank(configurationService.get(ConfigurationKey.MAIL_HOST, false, null));
    }

    @Override
    @Transactional
    public Ack subscribe(Map<Entity, Integer> entities) {
        // Gets the current user
        int userId = securityUtils.getCurrentAccountId();
        if (userId >= 0) {
            Ack ack = Ack.OK;
            // Subscribes to each entity
            for (Map.Entry<Entity, Integer> entry : entities.entrySet()) {
                Entity entity = entry.getKey();
                int entityId = entry.getValue();
                ack = ack.and(
                        subscriptionDao.subscribe(userId, entity, entityId)
                );
            }
            // OK
            return ack;
        } else {
            return Ack.NOK;
        }
    }
}
