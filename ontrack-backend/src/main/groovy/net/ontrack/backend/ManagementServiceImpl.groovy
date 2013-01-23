package net.ontrack.backend

import java.lang.invoke.MethodHandleImpl.BindCaller.T

import javax.sql.DataSource
import javax.validation.Validator

import net.ontrack.backend.db.SQL
import net.ontrack.core.model.ProjectGroupCreationForm
import net.ontrack.core.model.ProjectGroupSummary
import net.ontrack.service.ManagementService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ManagementServiceImpl extends AbstractServiceImpl implements ManagementService {

	@Autowired
	public ManagementServiceImpl(DataSource dataSource, Validator validator) {
		super(dataSource, validator);
	}

	@Override
	@Transactional
	public ProjectGroupSummary createProjectGroup(ProjectGroupCreationForm form) {
		// TODO Validation
		// Query
		int id = dbCreate (SQL.PROJECT_GROUP_CREATE, ["name": form.name, "description": form.description])
		// OK
		new ProjectGroupSummary(id, form.name, form.description)
	}
}
