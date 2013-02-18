package net.ontrack.backend

import net.ontrack.backend.db.SQL
import net.ontrack.backend.db.SQLUtils
import net.ontrack.core.model.*
import net.ontrack.core.security.SecurityRoles
import net.ontrack.core.security.SecurityUtils
import net.ontrack.core.validation.NameDescription
import net.ontrack.service.ControlService
import net.ontrack.service.EventService
import net.ontrack.service.ManagementService
import net.ontrack.service.model.Event
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.sql.DataSource
import javax.validation.Validator

@Service
class ControlServiceImpl extends AbstractServiceImpl implements ControlService {
	
	private final ManagementService managementService
    private final SecurityUtils securityUtils

	@Autowired
	public ControlServiceImpl(DataSource dataSource, Validator validator, EventService auditService, ManagementService managementService, SecurityUtils securityUtils) {
		super(dataSource, validator, auditService)
		this.managementService = managementService
        this.securityUtils = securityUtils
	}
	
	@Override
	@Transactional
    @Secured([SecurityRoles.CONTROLLER,SecurityRoles.ADMINISTRATOR])
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
    @Secured([SecurityRoles.CONTROLLER,SecurityRoles.ADMINISTRATOR])
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
			.withValue("status", validationRun.status.name())
			)
		// Gets the summary
		return run
	}

    @Override
	@Transactional
    @Secured([SecurityRoles.USER,SecurityRoles.CONTROLLER,SecurityRoles.ADMINISTRATOR])
	public ValidationRunStatusSummary createValidationRunStatus (int validationRun, ValidationRunStatusCreationForm validationRunStatus) {
		// TODO Validation of the status
		// Author
        def signature = securityUtils.getCurrentSignature()
		// Creation
		int id = dbCreate (SQL.VALIDATION_RUN_STATUS_CREATE, [
			"validationRun": validationRun,
			"status": validationRunStatus.status.name(),
			"description": validationRunStatus.description,
			"author": signature.name,
			"authorId": signature.id,
			"statusTimestamp": SQLUtils.toTimestamp(SQLUtils.now()) 
			])
		// OK
		return new ValidationRunStatusSummary(id, signature.name, validationRunStatus.status, validationRunStatus.description)
	}

}
