package net.ontrack.backend

import javax.sql.DataSource
import javax.validation.Validator

import net.ontrack.backend.db.SQL
import net.ontrack.backend.db.SQLUtils;
import net.ontrack.core.model.BuildCreationForm
import net.ontrack.core.model.BuildSummary
import net.ontrack.core.model.EventType
import net.ontrack.core.model.ValidationRunCreationForm
import net.ontrack.core.model.ValidationRunStatusCreationForm
import net.ontrack.core.model.ValidationRunStatusSummary
import net.ontrack.core.model.ValidationRunSummary
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
	
	@Override
	@Transactional
	public ValidationRunSummary createValidationRun(int build, int validationStamp, ValidationRunCreationForm validationRun) {
		// Run itself
		int id = dbCreate (SQL.VALIDATION_RUN_CREATE, ["build": build, "validationStamp": validationStamp, "description": validationRun.description])
		// First status
		createValidationRunStatus(id, new ValidationRunStatusCreationForm(validationRun.status, validationRun.description))
		// Summary
		def run = managementService.getValidationRun(id)
		// Event
		event(Event.of(EventType.VALIDATION_RUN_CREATED)
			.withProject(run.build.branch.project.id)
			.withBranch(run.build.branch.id)
			.withValidationStamp(validationStamp)
			.withBuild(build)
			.withValidationRun(id)
			.withValue("status", validationRun.status)
			)
		// Gets the summary
		return run
	}
	
	@Transactional
	public ValidationRunStatusSummary createValidationRunStatus (int validationRun, ValidationRunStatusCreationForm validationRunStatus) {
		// TODO Validation of the status
		// TODO Author
		// Creation
		int id = dbCreate (SQL.VALIDATION_RUN_STATUS_CREATE, [
			"validationRun": validationRun,
			"status": validationRunStatus.status,
			"description": validationRunStatus.description,
			"author": "",
			"authorId": null,
			"statusTimestamp": SQLUtils.toTimestamp(SQLUtils.now()) 
			])
		// OK
		return new ValidationRunStatusSummary(id, validationRunStatus.status, validationRunStatus.description)
	}

}
