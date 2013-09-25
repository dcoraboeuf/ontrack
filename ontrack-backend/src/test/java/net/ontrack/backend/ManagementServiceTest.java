package net.ontrack.backend;

import net.ontrack.core.model.*;
import net.ontrack.service.EventService;
import net.ontrack.service.ManagementService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import static org.junit.Assert.*;

public class ManagementServiceTest extends AbstractValidationTest {

    @Autowired
    private ManagementService service;
    @Autowired
    private EventService eventService;

    @Test
    public void createProject() throws Exception {
        final String projectName = uid("PRJ");
        ProjectSummary summary = asAdmin().call(new Callable<ProjectSummary>() {
            @Override
            public ProjectSummary call() throws Exception {
                return service.createProject(new ProjectCreationForm(projectName, "My description"));
            }
        });
        assertNotNull(summary);
        assertEquals(projectName, summary.getName());
        assertEquals("My description", summary.getDescription());
    }

    @Test
    public void createProject_name_format() throws Exception {
        validateNOK(" - Name: must match \"[A-Za-z0-9_.-]*\"\n",
                new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        return asAdmin().call(new Callable<Void>() {
                            @Override
                            public Void call() throws Exception {
                                service.createProject(new ProjectCreationForm("Project 1", "My description"));
                                return null;
                            }
                        });
                    }
                });
    }

    @Test
    public void loadProject() throws Exception {
        // Creates the project
        ProjectSummary project = doCreateProject();
        // Gets the project
        ProjectSummary loadedProject = service.getProject(project.getId());
        assertEquals(project.getName(), loadedProject.getName());
        assertEquals(project.getDescription(), loadedProject.getDescription());
        // Gets the audit
        List<ExpandedEvent> events = eventService.list(new EventFilter(0, 1));
        assertNotNull(events);
        assertFalse(events.isEmpty());
        ExpandedEvent event = events.get(0);
        assertEquals(EventType.PROJECT_CREATED, event.getEventType());
        assertEquals(
                Collections.singletonMap(
                        Entity.PROJECT,
                        new EntityStub(Entity.PROJECT, project.getId(), project.getName())),
                event.getEntities());
    }

    @Test
    public void deleteProject() throws Exception {
        // Creates the project
        final ProjectSummary project = doCreateProject();
        // Deletes the project
        Ack ack = asAdmin().call(new Callable<Ack>() {
            @Override
            public Ack call() throws Exception {
                return service.deleteProject(project.getId());
            }
        });
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
                        project.getName()),
                event.getValues());
    }

    @Test
    public void getBranchList() throws Exception {
        ProjectSummary p = doCreateProject();
        BranchSummary b1 = doCreateBranch(p.getId());
        BranchSummary b2 = doCreateBranch(p.getId());
        List<BranchSummary> branches = service.getBranchList(p.getId());
        assertNotNull(branches);
        assertEquals(2, branches.size());
        assertEquals(b1.getName(), branches.get(0).getName());
        assertEquals(p.getName(), branches.get(0).getProject().getName());
        assertEquals(b2.getName(), branches.get(1).getName());
        assertEquals(p.getName(), branches.get(1).getProject().getName());
    }

    @Test
    public void getBranch() throws Exception {
        BranchSummary branch = doCreateBranch();
        BranchSummary loadedBranch = service.getBranch(branch.getId());
        assertNotNull(loadedBranch);
        assertEquals(branch.getName(), loadedBranch.getName());
        assertEquals(branch.getDescription(), loadedBranch.getDescription());
        assertEquals(branch.getProject().getName(), loadedBranch.getProject().getName());
    }

    @Test
    public void createBranch() throws Exception {
        final ProjectSummary project = doCreateProject();
        final String branchName = uid("BCH");
        BranchSummary branch = asAdmin().call(new Callable<BranchSummary>() {
            @Override
            public BranchSummary call() throws Exception {
                return service.createBranch(project.getId(), new BranchCreationForm(branchName, "Project 2 branch 2"));
            }
        });
        assertNotNull(branch);
        assertEquals(branchName, branch.getName());
        assertEquals("Project 2 branch 2", branch.getDescription());
        assertEquals(project.getName(), branch.getProject().getName());
    }

    @Test
    public void getEntityId() throws Exception {
        ProjectSummary p = doCreateProject();
        assertEquals(p.getId(), service.getEntityId(Entity.PROJECT, p.getName(), Collections.<Entity, Integer>emptyMap()));
    }

    @Test(expected = EntityNameNotFoundException.class)
    public void getEntityId_not_found() {
        service.getEntityId(Entity.PROJECT, "PROJECTX", Collections.<Entity, Integer>emptyMap());
    }

}
