package net.ontrack.backend;

import net.ontrack.backend.dao.SubscriptionDao;
import net.ontrack.core.model.Ack;
import net.ontrack.core.model.Entity;
import net.ontrack.core.model.ExpandedEvent;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.service.GUIEventService;
import net.ontrack.service.MessageService;
import net.ontrack.service.SubscriptionService;
import net.ontrack.service.TemplateService;
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
    private final GUIEventService guiEventService;
    private final MessageService messageService;
    private final TemplateService templateService;

    @Autowired
    public DefaultSubscriptionService(SecurityUtils securityUtils, ConfigurationService configurationService, SubscriptionDao subscriptionDao, GUIEventService guiEventService, MessageService messageService, TemplateService templateService) {
        this.securityUtils = securityUtils;
        this.configurationService = configurationService;
        this.subscriptionDao = subscriptionDao;
        this.guiEventService = guiEventService;
        this.messageService = messageService;
        this.templateService = templateService;
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

    /**
     * Sends a message for this event
     */
    @Override
    @Transactional(readOnly = true)
    public void publish(ExpandedEvent event) {
        // FIXME Publication
    }
}
