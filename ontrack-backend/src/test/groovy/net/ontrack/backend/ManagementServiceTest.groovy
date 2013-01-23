package net.ontrack.backend

import java.lang.invoke.MethodHandleImpl.BindCaller.T

import net.ontrack.core.model.ProjectGroupCreationForm
import net.ontrack.service.ManagementService

import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class ManagementServiceTest extends AbstractValidationTest {
	
	@Autowired
	private ManagementService service
	
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

}
