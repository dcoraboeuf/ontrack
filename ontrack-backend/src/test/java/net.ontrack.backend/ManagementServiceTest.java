package net.ontrack.backend;

import net.ontrack.backend.security.AccountAuthentication;
import net.ontrack.core.model.*;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.service.EventService;
import net.ontrack.service.ManagementService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ManagementServiceTest extends AbstractValidationTest {

    @Autowired
    private ManagementService service;

    @Autowired
    private EventService eventService;

    @Before
    public void asAdmin() {
        AccountAuthentication authentication = new AccountAuthentication(new Account(1, "admin", "Administrator", "admin@ontrack.net", SecurityRoles.ADMINISTRATOR, "builtin"));
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @After
    public void noUser() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void createProject() {
        ProjectSummary summary = service.createProject(new ProjectCreationForm("PRJ1", "My description"));
        assertNotNull(summary);
        assertEquals("PRJ1", summary.getName());
        assertEquals("My description", summary.getDescription());
    }

    @Test
    public void createProject_name_format() {
        validateNOK(" - Name: must match \"[A-Za-z0-9_.]*\"\n", new Runnable() {
            @Override
            public void run() {
                service.createProject(new ProjectCreationForm("Project 1", "My description"));
            }
        });
    }

    @Test
    public void loadProject() {
        // Creates the project
        ProjectSummary summary = service.createProject(new ProjectCreationForm("LOAD1", "My description"));
        assertNotNull(summary);
        assertEquals("LOAD1", summary.getName());
        assertEquals("My description", summary.getDescription());
        // Gets the project
        summary = service.getProject(summary.getId());
        assertEquals("LOAD1", summary.getName());
        assertEquals("My description", summary.getDescription());
        // Gets the audit
        List<ExpandedEvent> events = eventService.list(new EventFilter(0, 1));
        assertNotNull(events);
        assertFalse(events.isEmpty());
        ExpandedEvent event = events.get(0);
        assertEquals(EventType.PROJECT_CREATED, event.getEventType());
        assertEquals(
                Collections.singletonMap(
                        Entity.PROJECT,
                        new EntityStub(Entity.PROJECT, summary.getId(), "LOAD1")),
                event.getEntities());
    }

    @Test
    public void deleteProject() {
        // Creates the project
        ProjectSummary summary = service.createProject(new ProjectCreationForm("DELETE1", "My description"));
        assertNotNull(summary);
        assertEquals("DELETE1", summary.getName());
        assertEquals("My description", summary.getDescription());
        // Deletes the project
        Ack ack = service.deleteProject(summary.getId());
        assertTrue(ack.isSuccess());
        // Gets the audit
        List<ExpandedEvent> events = eventService.list(new EventFilter(0, 1));
        assertNotNull(events);
        assertFalse(events.isEmpty());
        // Deletion
        ExpandedEvent event = events.get(0);
        assertEquals(EventType.PROJECT_DELETED, event.getEventType());
        assertEquals(
                Collections.singletonMap(
                        "project",
                        "DELETE1"),
                event.getValues());
    }

    @Test
    public void getBranchList() {
        List<BranchSummary> branches = service.getBranchList(1);
        assertNotNull(branches);
        assertEquals(2, branches.size());
        assertEquals("BRANCH1", branches.get(0).getName());
        assertEquals("Project 1 branch 1", branches.get(0).getDescription());
        assertEquals("PROJECT1", branches.get(0).getProject().getName());
        assertEquals("BRANCH2", branches.get(1).getName());
        assertEquals("Project 1 branch 2", branches.get(1).getDescription());
        assertEquals("PROJECT1", branches.get(1).getProject().getName());
    }

    @Test
    public void getBranch() {
        BranchSummary branch = service.getBranch(3);
        assertNotNull(branch);
        assertEquals("BRANCH1", branch.getName());
        assertEquals("Project 2 branch 1", branch.getDescription());
        assertEquals("PROJECT2", branch.getProject().getName());
    }

    @Test
    public void createBranch() {
        BranchSummary branch = service.createBranch(2, new BranchCreationForm("BRANCH2", "Project 2 branch 2"));
        assertNotNull(branch);
        assertEquals("BRANCH2", branch.getName());
        assertEquals("Project 2 branch 2", branch.getDescription());
        assertEquals("PROJECT2", branch.getProject().getName());
    }

    @Test
    public void getEntityId() {
        assertEquals(1, service.getEntityId(Entity.PROJECT, "PROJECT1", Collections.<Entity, Integer>emptyMap()));
        assertEquals(2, service.getEntityId(Entity.PROJECT, "PROJECT2", Collections.<Entity, Integer>emptyMap()));
        assertEquals(1, service.getEntityId(Entity.PROJECT_GROUP, "GROUP1", Collections.<Entity, Integer>emptyMap()));
        assertEquals(2, service.getEntityId(Entity.PROJECT_GROUP, "GROUP2", Collections.<Entity, Integer>emptyMap()));
    }

    @Test(expected = EntityNameNotFoundException.class)
    public void getEntityId_not_found() {
        service.getEntityId(Entity.PROJECT, "PROJECTX", Collections.<Entity, Integer>emptyMap());
    }

}
