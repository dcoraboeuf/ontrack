package net.ontrack.backend;

import net.ontrack.backend.dao.AccountDao;
import net.ontrack.backend.dao.EntityDao;
import net.ontrack.backend.dao.SubscriptionDao;
import net.ontrack.backend.dao.model.TAccount;
import net.ontrack.core.config.CoreConfig;
import net.ontrack.core.model.*;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.core.support.MapBuilder;
import net.ontrack.service.GUIEventService;
import net.ontrack.service.GUIService;
import net.ontrack.service.MessageService;
import net.ontrack.service.TemplateService;
import net.ontrack.service.model.MessageChannel;
import net.ontrack.service.model.MessageDestination;
import net.ontrack.service.model.TemplateModel;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

import static org.mockito.Mockito.*;

public class DefaultSubscriptionServiceTest {

    @Test
    public void publish_with_status() {
        // Mocks
        SubscriptionDao subscriptionDao = mock(SubscriptionDao.class);
        AccountDao accountDao = mock(AccountDao.class);
        GUIEventService guiEventService = mock(GUIEventService.class);
        MessageService messageService = mock(MessageService.class);
        TemplateService templateService = mock(TemplateService.class);
        // Service to test
        DefaultSubscriptionService service = new DefaultSubscriptionService(
                mock(SecurityUtils.class),
                mock(ConfigurationService.class),
                subscriptionDao,
                accountDao,
                mock(EntityDao.class),
                guiEventService,
                mock(GUIService.class),
                messageService,
                templateService,
                new CoreConfig().strings()
        );
        // Event to send
        ExpandedEvent event = new ExpandedEvent(
                100,
                "The Author",
                EventType.VALIDATION_RUN_CREATED,
                new DateTime(),
                MapBuilder
                        .of(Entity.PROJECT, new EntityStub(Entity.PROJECT, 1, "PRJ"))
                        .with(Entity.BRANCH, new EntityStub(Entity.BRANCH, 1, "BRCH"))
                        .get(),
                MapBuilder.
                        of("status", Status.FAILED.name())
                        .get()
        );
        // List of accounts
        when(subscriptionDao.findAccountIds(
                MapBuilder.of(Entity.PROJECT, 1).with(Entity.BRANCH, 1).get()
        )).thenReturn(Arrays.asList(1));
        // Account access
        when(accountDao.getByID(1)).thenReturn(new TAccount(
                22,
                "the_author",
                "The Author",
                "the_author@test.com",
                SecurityRoles.USER,
                "builtin",
                Locale.ENGLISH
        ));
        // GUI event
        when(guiEventService.toGUIEvent(
                eq(event),
                eq(Locale.ENGLISH),
                any(DateTime.class)
        )).thenReturn(
                new GUIEvent(
                        100,
                        "The Author",
                        EventType.VALIDATION_RUN_CREATED,
                        "The timestamp",
                        "The elapsed time",
                        "The message content with <b>html</b>.",
                        "icon-class",
                        Status.FAILED.name()
                )
        );
        // Template
        when(templateService.generate(
                eq("event.html"),
                eq(Locale.ENGLISH),
                any(TemplateModel.class)
        )).thenReturn("The message content with <b>html</b>.");
        // Publishes an event
        service.publish(event);
        // Checks the reception
        verify(messageService, times(1)).sendMessage(
                new Message(
                        "ontrack event",
                        new MessageContent(
                                MessageContentType.HTML,
                                "The message content with <b>html</b>.",
                                Collections.<String, String>emptyMap()
                        )
                ),
                new MessageDestination(
                        MessageChannel.EMAIL,
                        Arrays.asList("the_author@test.com")
                )
        );
    }

}
