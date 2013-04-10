package net.ontrack.backend;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.ontrack.backend.dao.AccountDao;
import net.ontrack.backend.dao.EntityDao;
import net.ontrack.backend.dao.SubscriptionDao;
import net.ontrack.backend.dao.model.TAccount;
import net.ontrack.core.model.*;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.service.*;
import net.ontrack.service.model.MessageChannel;
import net.ontrack.service.model.MessageDestination;
import net.ontrack.service.model.TemplateModel;
import net.sf.jstring.Strings;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.String.format;

@Service
public class DefaultSubscriptionService implements SubscriptionService {

    private final Logger logger = LoggerFactory.getLogger(SubscriptionService.class);

    private final SecurityUtils securityUtils;
    private final ConfigurationService configurationService;
    private final SubscriptionDao subscriptionDao;
    private final AccountDao accountDao;
    private final EntityDao entityDao;
    private final GUIEventService guiEventService;
    private final GUIService guiService;
    private final MessageService messageService;
    private final TemplateService templateService;
    private final Strings strings;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor(
            new ThreadFactoryBuilder().setDaemon(true).setNameFormat("Subscription %s").build()
    );

    @Autowired
    public DefaultSubscriptionService(SecurityUtils securityUtils, ConfigurationService configurationService, SubscriptionDao subscriptionDao, AccountDao accountDao, EntityDao entityDao, GUIEventService guiEventService, GUIService guiService, MessageService messageService, TemplateService templateService, Strings strings) {
        this.securityUtils = securityUtils;
        this.configurationService = configurationService;
        this.subscriptionDao = subscriptionDao;
        this.accountDao = accountDao;
        this.entityDao = entityDao;
        this.guiEventService = guiEventService;
        this.guiService = guiService;
        this.messageService = messageService;
        this.templateService = templateService;
        this.strings = strings;
    }

    @Override
    public boolean isEnabled() {
        return StringUtils.isNotBlank(configurationService.get(ConfigurationKey.MAIL_HOST, false, null));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSubscribed(int accountId, Set<EntityID> entities) {
        if (entities.isEmpty()) {
            return false;
        } else {
            Set<EntityID> subscribedEntities = subscriptionDao.findEntitiesByAccount(accountId);
            return subscribedEntities.containsAll(entities);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionEntityInfo getSubscriptionEntityInfo(int accountId, Entity entity, int entityId) {
        if (isSubscribed(accountId, Collections.singleton(new EntityID(entity, entityId)))) {
            // Loads the entity name
            String name = entityDao.getEntityName(entity, entityId);
            EntityStub entityStub = new EntityStub(entity, entityId, name);
            // Confirmation message
            // TODO Gets the locale from the account (see ticket #81)
            String message = getUnsubscriptionConfirmationMessage(entityStub, Locale.ENGLISH);
            // OK
            return new SubscriptionEntityInfo(entityStub, message);
        } else {
            return SubscriptionEntityInfo.none();
        }
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

    @Override
    @Transactional
    public Ack unsubscribe(Map<Entity, Integer> entities) {
        // Gets the current user
        int userId = securityUtils.getCurrentAccountId();
        if (userId >= 0) {
            Ack ack = Ack.OK;
            // Unsubscribes from each entity
            for (Map.Entry<Entity, Integer> entry : entities.entrySet()) {
                Entity entity = entry.getKey();
                int entityId = entry.getValue();
                ack = ack.and(
                        subscriptionDao.unsubscribe(userId, entity, entityId)
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
    public void publish(final ExpandedEvent event) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    doPublish(event);
                } catch (Exception ex) {
                    logger.error("[publish] Error on publishing", ex);
                }
            }
        });
    }

    protected void doPublish(ExpandedEvent event) {
        logger.debug("[publish] event={}", event.getId());
        // Gets the IDs of the entities
        Map<Entity, Integer> entityIds = Maps.transformValues(
                event.getEntities(),
                EntityStub.FN_GET_ID);
        // Collects all users that need to be notified for this event
        Collection<TAccount> accounts = Lists.transform(
                subscriptionDao.findAccountIds(entityIds),
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
        // If no account, does nothing
        if (accounts.isEmpty()) {
            logger.debug("[publish] event={}, no-account", event.getId());
            return;
        }
        // TODO Collects all the languages (not possible yet, see ticket #81)
        Locale locale = Locale.ENGLISH;
        logger.debug("[publish] event={}, locale={}", event.getId(), locale);
        // TODO Generates one message per language (see ticket #81)
        for (TAccount account : accounts) {
            String email = account.getEmail();
            // Gets the GUI version
            GUIEvent guiEvent = guiEventService.toGUIEvent(event, locale, DateTime.now(DateTimeZone.UTC));
            // Initial template
            TemplateModel model = new TemplateModel();
            model.add("event", guiEvent);
            // Gets the title
            String title = strings.get(locale, "event.message");
            model.add("title", title);
            // Gets the list of entities the account is registered to
            Set<EntityID> subscriptions = subscriptionDao.findEntitiesByAccount(account.getId());
            // Unsubscription links
            // We actually need one distinct link per entity
            Collection<NamedLink> links = getUnsubscriptionLinks(locale, event.getEntities().values(), subscriptions);
            model.add("links", links);
            // Generates the message HTML content
            String content = templateService.generate("event.html", locale, model);
            // Creates a HTML message
            Message message = new Message(
                    title,
                    new MessageContent(
                            MessageContentType.HTML,
                            content));
            // Publication
            logger.debug("[publish] event={}, locale={}, account={}", event.getId(), locale, email);
            messageService.sendMessage(
                    message,
                    new MessageDestination(
                            MessageChannel.EMAIL,
                            Collections.singletonList(email)
                    )
            );
        }
    }

    private Collection<NamedLink> getUnsubscriptionLinks(final Locale locale, Collection<EntityStub> entities, final Set<EntityID> subscriptions) {
        return Collections2.transform(
                Collections2.filter(
                        entities,
                        new Predicate<EntityStub>() {
                            @Override
                            public boolean apply(EntityStub stub) {
                                return subscriptions.contains(new EntityID(stub.getEntity(), stub.getId()));
                            }
                        }
                ),
                new Function<EntityStub, NamedLink>() {
                    @Override
                    public NamedLink apply(EntityStub stub) {
                        return new NamedLink(
                                getUnsubscriptionLink(stub),
                                getUnsubscriptionConfirmationMessage(stub, locale)
                        );
                    }
                }
        );
    }

    private String getUnsubscriptionConfirmationMessage(EntityStub stub, Locale locale) {
        return strings.get(
                locale,
                format(
                        "event.unsubscription.%s",
                        stub.getEntity().name()),
                stub.getName()
        );
    }

    private String getUnsubscriptionLink(EntityStub stub) {
        return guiService.toGUI(format("admin/unsubscribe/%s/%d", stub.getEntity().name(), stub.getId()));
    }
}
