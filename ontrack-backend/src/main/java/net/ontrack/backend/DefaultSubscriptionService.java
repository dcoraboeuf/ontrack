package net.ontrack.backend;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.ontrack.backend.dao.AccountDao;
import net.ontrack.backend.dao.SubscriptionDao;
import net.ontrack.backend.dao.model.TAccount;
import net.ontrack.core.model.*;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.service.GUIEventService;
import net.ontrack.service.MessageService;
import net.ontrack.service.SubscriptionService;
import net.ontrack.service.TemplateService;
import net.ontrack.service.model.MessageChannel;
import net.ontrack.service.model.MessageDestination;
import net.ontrack.service.model.TemplateModel;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

@Service
public class DefaultSubscriptionService implements SubscriptionService {

    private final SecurityUtils securityUtils;
    private final ConfigurationService configurationService;
    private final SubscriptionDao subscriptionDao;
    private final AccountDao accountDao;
    private final GUIEventService guiEventService;
    private final MessageService messageService;
    private final TemplateService templateService;

    @Autowired
    public DefaultSubscriptionService(SecurityUtils securityUtils, ConfigurationService configurationService, SubscriptionDao subscriptionDao, AccountDao accountDao, GUIEventService guiEventService, MessageService messageService, TemplateService templateService) {
        this.securityUtils = securityUtils;
        this.configurationService = configurationService;
        this.subscriptionDao = subscriptionDao;
        this.accountDao = accountDao;
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
        // Collects all users that need to be notified for this event
        Collection<TAccount> accounts = Lists.transform(
                subscriptionDao.findAccountIds(
                        Maps.transformValues(
                                event.getEntities(),
                                EntityStub.FN_GET_ID)
                ),
                new Function<Integer, TAccount>() {
                    @Override
                    public TAccount apply(Integer id) {
                        return accountDao.getByID(id);
                    }
                }
        );
        // Filters the accounts on those who have an email
        accounts = Collections2.filter(
                accounts,
                new Predicate<TAccount>() {
                    @Override
                    public boolean apply(TAccount t) {
                        return StringUtils.isNotBlank(t.getEmail());
                    }
                }
        );
        // TODO Collects all the languages (not possible yet, see ticket #81)
        // TODO Generates one message per language (see ticket #81)
        // Gets the GUI version
        GUIEvent guiEvent = guiEventService.toGUIEvent(event, Locale.ENGLISH, DateTime.now(DateTimeZone.UTC));
        // Initial template
        TemplateModel model = new TemplateModel();
        model.add("event", guiEvent);
        // Sends for each user
        for (TAccount account : accounts) {
            // Completes the model for the account
            model.add("account", account);
            // Generates the message HTML content
            String content = templateService.generate("event.html", Locale.ENGLISH, model);
            // TODO Gets the title
            String title = "TODO Event";
            // TODO Creates a HTML message
            Message message = new Message(
                    title,
                    new MessageContent(content));
            // Publication
            messageService.sendMessage(
                    message,
                    new MessageDestination(
                            MessageChannel.EMAIL,
                            account.getEmail()
                    )
            );
        }
    }
}
