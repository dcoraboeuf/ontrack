package net.ontrack.backend

import net.ontrack.backend.security.AccountAuthentication
import net.ontrack.core.model.*
import net.ontrack.core.security.SecurityRoles
import net.ontrack.service.EventService
import net.ontrack.service.ManagementService
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class ManagementServiceTest extends AbstractValidationTest {

    @Autowired
    private ManagementService service

    @Autowired
    private EventService eventService

    @Before
    public void asAdmin() {
        def authentication = new AccountAuthentication(new Account(1, "admin", "Administrator", "admin@ontrack.net", SecurityRoles.ADMINISTRATOR, "builtin"))
        def securityContext = mock(SecurityContext)
        when(securityContext.getAuthentication()).thenReturn(authentication)
        SecurityContextHolder.setContext(securityContext)
    }

    @After
    public void noUser() {
        SecurityContextHolder.clearContext()
    }

    @Test
    void getProjectGroupList() {
        def list = service.getProjectGroupList()
        assert list != null
        assert [1, 2] == list*.id
        assert ["GROUP1", "GROUP2"] == list*.name
        assert ["Group 1", "Group 2"] == list*.description
    }

    @Test
    void createProjectGroup() {
        def summary = service.createProjectGroup(new ProjectGroupCreationForm("GRP1", "My description"))
        assert summary != null
        assert "GRP1" == summary.name
        assert "My description" == summary.description
    }

    @Test
    void createProjectGroup_name_null() {
        validateNOK(" - Name: may not be null\n") {
            service.createProjectGroup(new ProjectGroupCreationForm(null, "My description"))
        }
    }

    @Test
    void createProjectGroup_name_empty() {
        validateNOK(" - Name: size must be between 1 and 80\n") {
            service.createProjectGroup(new ProjectGroupCreationForm("", "My description"))
        }
    }

    @Test
    void createProjectGroup_description_null() {
        validateNOK(" - Description: may not be null\n") {
            service.createProjectGroup(new ProjectGroupCreationForm("NAME", null))
        }
    }

    @Test
    void createProject() {
        def summary = service.createProject(new ProjectCreationForm("PRJ1", "My description"))
        assert summary != null
        assert "PRJ1" == summary.name
        assert "My description" == summary.description
    }

    @Test
    void createProject_name_format() {
        validateNOK(""" - Name: must match "[A-Za-z0-9_.]*"\n""") {
            service.createProject(new ProjectCreationForm("Project1", "My description"))
        }
    }

    @Test
    void loadProject() {
        // Creates the project
        def summary = service.createProject(new ProjectCreationForm("LOAD1", "My description"))
        assert summary != null
        assert "LOAD1" == summary.name
        assert "My description" == summary.description
        // Gets the project
        summary = service.getProject(summary.id)
        assert "LOAD1" == summary.name
        assert "My description" == summary.description
        // Gets the audit
        def events = eventService.list(new EventFilter(0, 1))
        assert events != null && !events.empty
        def event = events.get(0)
        assert EventType.PROJECT_CREATED == event.eventType
        assert Collections.singletonMap(Entity.PROJECT, new EntityStub(Entity.PROJECT, summary.id, "LOAD1")) == event.entities
    }

    @Test
    void deleteProject() {
        // Creates the project
        def summary = service.createProject(new ProjectCreationForm("DELETE1", "My description"))
        assert summary != null
        assert "DELETE1" == summary.name
        assert "My description" == summary.description
        // Deletes the project
        def ack = service.deleteProject(summary.id)
        assert ack.success
        // Gets the audit
        def events = eventService.list(new EventFilter(0, 1))
        assert events != null && !events.empty
        // Deletion
        def event = events.get(0)
        assert EventType.PROJECT_DELETED == event.eventType
        assert ["project": "DELETE1"] == event.values
    }

    @Test
    void getBranchList() {
        def branches = service.getBranchList(1)
        assert branches != null && branches.size() == 2
        assert ["BRANCH1", "BRANCH2"] == branches*.name
        assert ["Project 1 branch 1", "Project 1 branch 2"] == branches*.description
        assert ["PROJECT1", "PROJECT1"] == branches*.project*.name
    }

    @Test
    void getBranch() {
        def branch = service.getBranch(3)
        assert branch != null
        assert "BRANCH1" == branch.name
        "Project 2 branch 1" == branch.description
        assert "PROJECT2" == branch.project.name
    }

    @Test
    void createBranch() {
        def branch = service.createBranch(2, new BranchCreationForm("BRANCH2", "Project 2 branch 2"))
        assert branch != null
        assert "BRANCH2" == branch.name
        "Project 2 branch 2" == branch.description
    }

    @Test
    void getEntityId() {
        assert 1 == service.getEntityId(Entity.PROJECT, "PROJECT1", [:])
        assert 2 == service.getEntityId(Entity.PROJECT, "PROJECT2", [:])
        assert 1 == service.getEntityId(Entity.PROJECT_GROUP, "GROUP1", [:])
        assert 2 == service.getEntityId(Entity.PROJECT_GROUP, "GROUP2", [:])
    }

    @Test(expected = EntityNameNotFoundException)
    void getEntityId_not_found() {
        service.getEntityId(Entity.PROJECT, "PROJECTX", [:])
    }

}
