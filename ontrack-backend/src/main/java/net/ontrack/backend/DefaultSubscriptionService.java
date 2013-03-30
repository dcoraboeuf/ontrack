package net.ontrack.backend;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.ontrack.backend.dao.AccountDao;
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

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
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
    private final GUIEventService guiEventService;
    private final GUIService guiService;
    private final MessageService messageService;
    private final TemplateService templateService;
    private final Strings strings;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor(
            new ThreadFactoryBuilder().setDaemon(true).setNameFormat("Subscription %s").build()
    );

    @Autowired
    public DefaultSubscriptionService(SecurityUtils securityUtils, ConfigurationService configurationService, SubscriptionDao subscriptionDao, AccountDao accountDao, GUIEventService guiEventService, GUIService guiService, MessageService messageService, TemplateService templateService, Strings strings) {
        this.securityUtils = securityUtils;
        this.configurationService = configurationService;
        this.subscriptionDao = subscriptionDao;
        this.accountDao = accountDao;
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
        // Gets the GUI version
        GUIEvent guiEvent = guiEventService.toGUIEvent(event, locale, DateTime.now(DateTimeZone.UTC));
        // Initial template
        TemplateModel model = new TemplateModel();
        model.add("event", guiEvent);
        // Gets the title
        String title = strings.get(locale, "event.message");
        model.add("title", title);
        // Unsubscription link
        // We actually need one distinct link per entity
        Collection<NamedLink> links = getUnsubscriptionLinks(locale, event.getEntities().values());
        model.add("links", links);
        // Gets all the emails
        Collection<String> emails = Collections2.transform(
                accounts,
                new Function<TAccount, String>() {
                    @Override
                    public String apply(TAccount account) {
                        return account.getEmail();
                    }
                }
        );
        // Generates the message HTML content
        String content = templateService.generate("event.html", locale, model);
        // Creates a HTML message
        Message message = new Message(
                title,
                new MessageContent(
                        MessageContentType.HTML,
                        content));
        // Publication
        logger.debug("[publish] event={}, locale={}, accounts={}", new Object[]{event.getId(), locale, emails.size()});
        messageService.sendMessage(
                message,
                new MessageDestination(
                        MessageChannel.EMAIL,
                        emails
                )
        );
    }

    private Collection<NamedLink> getUnsubscriptionLinks(final Locale locale, Collection<EntityStub> entities) {
        return Collections2.transform(
                entities,
                new Function<EntityStub, NamedLink>() {
                    @Override
                    public NamedLink apply(EntityStub stub) {
                        return new NamedLink(
                                getUnsubscriptionLink(stub),
                                strings.get(
                                        locale,
                                        format(
                                                "event.unsubscription.%s",
                                                stub.getEntity().name()),
                                        stub.getName()
                                )
                        );
                    }
                }
        );
    }

    private String getUnsubscriptionLink(EntityStub stub) {
        return guiService.toGUI(format("admin/unsubscribe/%s/%d", stub.getEntity().name(), stub.getId()));
    }
}
