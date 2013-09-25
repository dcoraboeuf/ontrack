package net.ontrack.backend;

import net.ontrack.core.model.*;
import net.ontrack.core.support.MapBuilder;
import net.ontrack.service.SubscriptionService;
import net.ontrack.service.support.InMemoryPost;
import net.ontrack.test.AbstractIntegrationTest;
import net.ontrack.test.Helper;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class DefaultSubscriptionServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private SubscriptionService service;
    @Autowired
    private InMemoryPost inMemoryPost;

    @Test
    public void publish() throws IOException {
        // Event to send
        ExpandedEvent event = new ExpandedEvent(
                100,
                "Any author",
                EventType.BUILD_CREATED,
                new DateTime(),
                MapBuilder
                        .of(Entity.PROJECT, new EntityStub(Entity.PROJECT, 1, "PRJ"))
                        .with(Entity.BRANCH, new EntityStub(Entity.BRANCH, 1, "BRCH1"))
                        .with(Entity.BUILD, new EntityStub(Entity.BUILD, 1, "1"))
                        .get(),
                Collections.<String, String>emptyMap()
        );
        // Publishes the event
        service.publish(event);
        // Checks the posts
        Message message = inMemoryPost.getMessage("test@test.com");
        assertEquals("ontrack  - Le build 1 a été créé pour la branche BRCH1 du projet PRJ.", message.getTitle());
        assertEquals("HTML", message.getContent().getType().toString());
        String expectedHtml = Helper.getResourceAsString("/net/ontrack/backend/DefaultSubscriptionServiceIntegrationTest-message.html");
        assertEquals(expectedHtml, message.getContent().getText());
    }

}
