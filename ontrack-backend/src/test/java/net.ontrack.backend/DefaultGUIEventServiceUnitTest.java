package net.ontrack.backend;

import net.ontrack.core.model.*;
import net.ontrack.core.support.MapBuilder;
import net.ontrack.service.GUIService;
import net.sf.jstring.Strings;
import net.sf.jstring.support.StringsLoader;
import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DefaultGUIEventServiceUnitTest {

    private static Strings strings;
    private static GUIService guiService;

    @BeforeClass
    public static void beforeClass() {
        strings = StringsLoader.auto(Locale.ENGLISH, Locale.FRENCH);
        guiService = new MockGUIService();
    }

    @Test
    public void createLinkHref_project() {
        DefaultGUIEventService controller = dummy();
        String href = controller.createLinkHref(
                Entity.PROJECT,
                new EntityStub(Entity.PROJECT, 1001, "PROJ5"),
                Collections.<Entity, EntityStub>emptyMap()
        );
        assertEquals("http://test/gui/project/PROJ5", href);
    }

    @Test
    public void createLinkHref_branch() {
        DefaultGUIEventService controller = dummy();
        String href = controller.createLinkHref(
                Entity.BRANCH,
                new EntityStub(Entity.BRANCH, 2001, "BRANCH1"),
                Collections.singletonMap(
                        Entity.PROJECT,
                        new EntityStub(Entity.PROJECT, 1001, "PROJ6")));
        assertEquals("http://test/gui/project/PROJ6/branch/BRANCH1", href);
    }

    @Test
    public void createLinkHref_build() {
        DefaultGUIEventService controller = dummy();
        Map<Entity, EntityStub> context =
                MapBuilder.of(Entity.PROJECT, new EntityStub(Entity.PROJECT, 1001, "PROJ7"))
                        .with(Entity.BRANCH, new EntityStub(Entity.BRANCH, 2001, "BRANCH2"))
                        .get();
        String href = controller.createLinkHref(
                Entity.BUILD,
                new EntityStub(Entity.BUILD, 3001, "BUILD1"),
                context);
        assertEquals("http://test/gui/project/PROJ7/branch/BRANCH2/build/BUILD1", href);
    }

    @Test
    public void createLink_project_no_alternative() {
        DefaultGUIEventService controller = dummy();
        String href = controller.createLink(
                Entity.PROJECT,
                new EntityStub(Entity.PROJECT, 1001, "PROJ4"),
                null,
                Collections.<Entity, EntityStub>emptyMap());
        assertEquals("<a class=\"event-entity\" href=\"http://test/gui/project/PROJ4\">PROJ4</a>", href);
    }

    @Test
    public void createLink_project_alternative() {
        DefaultGUIEventService controller = dummy();
        String href = controller.createLink(
                Entity.PROJECT,
                new EntityStub(Entity.PROJECT, 1001, "PROJ3"),
                "te>st",
                Collections.<Entity, EntityStub>emptyMap());
        assertEquals("<a class=\"event-entity\" href=\"http://test/gui/project/PROJ3\">te&gt;st</a>", href);
    }

    @Test
    public void createLink_branch() {
        DefaultGUIEventService controller = dummy();
        String href = controller.createLink(
                Entity.BRANCH,
                new EntityStub(Entity.BRANCH, 2001, "1.x"),
                null,
                Collections.singletonMap(
                        Entity.PROJECT,
                        new EntityStub(Entity.PROJECT, 1001, "PROJ3")));
        assertEquals("<a class=\"event-entity\" href=\"http://test/gui/project/PROJ3/branch/1.x\">1.x</a>", href);
    }

    // TODO Use a proper exception
    @Test(expected = IllegalStateException.class)
    public void createLink_branch_no_project() {
        DefaultGUIEventService controller = dummy();
        controller.createLink(
                Entity.BRANCH,
                new EntityStub(Entity.BRANCH, 2001, "1.x"),
                null,
                Collections.<Entity, EntityStub>emptyMap());
    }

    @Test
    public void expandToken_entity_project() {
        DefaultGUIEventService controller = dummy();
        String value = controller.expandToken(
                "$PROJECT$",
                new ExpandedEvent(
                        1,
                        "Author",
                        EventType.PROJECT_CREATED,
                        new DateTime())
                        .withEntity(
                                Entity.PROJECT,
                                new EntityStub(Entity.PROJECT, 1, "PROJ1")));
        assertEquals("<a class=\"event-entity\" href=\"http://test/gui/project/PROJ1\">PROJ1</a>", value);
    }

    @Test
    public void expandToken_entity_project_with_alternative() {
        DefaultGUIEventService controller = dummy();
        String value = controller.expandToken(
                "$PROJECT|this project$",
                new ExpandedEvent(
                        1,
                        "Author",
                        EventType.PROJECT_CREATED,
                        new DateTime()
                ).withEntity(
                        Entity.PROJECT,
                        new EntityStub(Entity.PROJECT, 1, "PROJ3")));
        assertEquals("<a class=\"event-entity\" href=\"http://test/gui/project/PROJ3\">this project</a>", value);
    }

    @Test(expected = IllegalStateException.class)
    public void expandToken_entity_project_not_found() {
        DefaultGUIEventService controller = dummy();
        controller.expandToken(
                "$PROJECT$",
                new ExpandedEvent(1, "Author", EventType.PROJECT_CREATED, new DateTime()));
    }

    @Test
    public void expandToken_value_only() {
        DefaultGUIEventService controller = dummy();
        String value = controller.expandToken(
                "$project$",
                new ExpandedEvent(
                        1,
                        "Author",
                        EventType.PROJECT_DELETED,
                        new DateTime())
                        .withValue("project", "My > project"));
        assertEquals("<span class=\"event-value\">My &gt; project</span>", value);
    }

    @Test
    public void toGUIEvent_one_entity() {
        DefaultGUIEventService controller = dummy();
        GUIEvent event = controller.toGUIEvent(
                new ExpandedEvent(10, "Author", EventType.PROJECT_CREATED, new DateTime(2013, 1, 30, 10, 5, 30))
                        .withEntity(Entity.PROJECT, new EntityStub(Entity.PROJECT, 1001, "PROJ2")),
                Locale.ENGLISH,
                new DateTime(2013, 1, 30, 11, 10, 45));
        assertNotNull(event);
        assertEquals(10, event.getId());
        assertEquals(EventType.PROJECT_CREATED, event.getEventType());
        assertEquals("Jan 30, 2013 10:05:30 AM", event.getTimestamp());
        assertEquals("1 hour ago by Author", event.getElapsed());
        assertEquals(
                "Project <a class=\"event-entity\" href=\"http://test/gui/project/PROJ2\">PROJ2</a> has been created.",
                event.getHtml());
    }

    @Test
    public void toGUIEvent_two_entities() {
        DefaultGUIEventService controller = dummy();
        GUIEvent event = controller.toGUIEvent(
                new ExpandedEvent(10, "Author", EventType.BRANCH_CREATED,
                        new DateTime(2013, 1, 30, 10, 5, 30))
                        .withEntity(Entity.PROJECT, new EntityStub(Entity.PROJECT, 1001, "PROJ1"))
                        .withEntity(Entity.BRANCH, new EntityStub(Entity.BRANCH, 2001, "1.x")),
                Locale.ENGLISH, new DateTime(2013, 1, 30, 11, 10, 45));
        assertNotNull(event);
        assertEquals(10, event.getId());
        assertEquals(EventType.BRANCH_CREATED, event.getEventType());
        assertEquals("Jan 30, 2013 10:05:30 AM", event.getTimestamp());
        assertEquals("1 hour ago by Author", event.getElapsed());
        assertEquals(
                "Branch <a class=\"event-entity\" href=\"http://test/gui/project/PROJ1/branch/1.x\">1.x</a> has been created for the <a class=\"event-entity\" href=\"http://test/gui/project/PROJ1\">PROJ1</a> project.",
                event.getHtml());
    }

    protected DefaultGUIEventService dummy() {
        return new DefaultGUIEventService(guiService, strings);
    }

}
