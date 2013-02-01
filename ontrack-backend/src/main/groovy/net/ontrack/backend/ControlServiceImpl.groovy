package net.ontrack.backend

import javax.sql.DataSource
import javax.validation.Validator

import net.ontrack.backend.db.SQL
import net.ontrack.core.model.BuildCreationForm
import net.ontrack.core.model.BuildSummary
import net.ontrack.core.model.EventType
import net.ontrack.core.validation.NameDescription
import net.ontrack.service.ControlService
import net.ontrack.service.EventService
import net.ontrack.service.ManagementService
import net.ontrack.service.model.Event

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ControlServiceImpl extends AbstractServiceImpl implements ControlService {
	
	private final ManagementService managementService

	@Autowired
	public ControlServiceImpl(DataSource dataSource, Validator validator, EventService auditService, ManagementService managementService) {
		super(dataSource, validator, auditService)
		this.managementService = managementService
	}
	
	@Override
	@Transactional
	public BuildSummary createBuild(int branch, BuildCreationForm form) {
		// Validation
		validate(form, NameDescription.class)
		// Query
		int id = dbCreate (SQL.BUILD_CREATE, ["branch": branch, "name": form.name, "description": form.description])
		// Branch summary
		def theBranch = managementService.getBranch(branch)
		// Audit
		event(Event.of(EventType.BUILD_CREATED).withProject(theBranch.project.id).withBranch(theBranch.id).withBuild(id))
		// OK
		new BuildSummary(id, form.name, form.description, theBranch)
	}

}
