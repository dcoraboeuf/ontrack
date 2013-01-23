package net.ontrack.backend

import java.lang.invoke.MethodHandleImpl.BindCaller.T

import net.ontrack.core.model.ProjectGroupCreationForm
import net.ontrack.service.ManagementService
import net.ontrack.test.AbstractIntegrationTest

import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class ManagementServiceTest extends AbstractIntegrationTest {
	
	@Autowired
	private ManagementService service
	
	@Test
	void createProjectGroup() {
		def summary = service.createProjectGroup(new ProjectGroupCreationForm("My name", "My description"))
		assert summary != null
		assert "My name" == summary.name
		assert "My description" == summary.description
	}

}
