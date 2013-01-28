package net.ontrack.backend

import java.lang.invoke.MethodHandleImpl.BindCaller.T

import net.ontrack.core.model.ProjectCreationForm
import net.ontrack.core.model.ProjectGroupCreationForm
import net.ontrack.service.ManagementService

import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class ManagementServiceTest extends AbstractValidationTest {
	
	@Autowired
	private ManagementService service
	
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
		def summary = service.createProjectGroup(new ProjectGroupCreationForm("My name", "My description"))
		assert summary != null
		assert "My name" == summary.name
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
			service.createProjectGroup(new ProjectGroupCreationForm("Name", null))
		}
	}
	
	@Test
	void createProject() {
		def summary = service.createProject(new ProjectCreationForm("My name", "My description"))
		assert summary != null
		assert "My name" == summary.name
		assert "My description" == summary.description
	}
	
	@Test
	void loadProject() {
		def summary = service.createProject(new ProjectCreationForm("LOAD1", "My description"))
		assert summary != null
		assert "LOAD1" == summary.name
		assert "My description" == summary.description
		summary = service.getProject(summary.id)
		assert "LOAD1" == summary.name
		assert "My description" == summary.description
	}

}
