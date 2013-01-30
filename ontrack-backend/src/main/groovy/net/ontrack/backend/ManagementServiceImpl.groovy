package net.ontrack.backend

import java.lang.invoke.MethodHandleImpl.BindCaller.T
import java.sql.ResultSet

import javax.sql.DataSource
import javax.validation.Validator

import net.ontrack.backend.db.SQL
import net.ontrack.core.model.Ack
import net.ontrack.core.model.Entity
import net.ontrack.core.model.EventType
import net.ontrack.core.model.ProjectCreationForm
import net.ontrack.core.model.ProjectGroupCreationForm
import net.ontrack.core.model.ProjectGroupSummary
import net.ontrack.core.model.ProjectSummary
import net.ontrack.core.validation.NameDescription
import net.ontrack.service.EventService
import net.ontrack.service.ManagementService
import net.ontrack.service.model.Event

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ManagementServiceImpl extends AbstractServiceImpl implements ManagementService {

	@Autowired
	public ManagementServiceImpl(DataSource dataSource, Validator validator, EventService auditService) {
		super(dataSource, validator, auditService);
	}
	
	// Project groups
	
	@Override
	@Transactional(readOnly = true)
	public List<ProjectGroupSummary> getProjectGroupList() {
		return dbList(SQL.PROJECT_GROUP_LIST) { rs ->
			new ProjectGroupSummary(rs.getInt("id"), rs.getString("name"), rs.getString("description"))
		}
	}

	@Override
	@Transactional
	public ProjectGroupSummary createProjectGroup(ProjectGroupCreationForm form) {
		// Validation
		validate(form, NameDescription.class);
		// Query
		int id = dbCreate (SQL.PROJECT_GROUP_CREATE, ["name": form.name, "description": form.description])
		// Audit
		event(Event.of(EventType.PROJECT_GROUP_CREATED).withProjectGroup(id))
		// OK
		new ProjectGroupSummary(id, form.name, form.description)
	}
	
	// Projects
	
	ProjectSummary readProjectSummary (ResultSet rs) {
		return new ProjectSummary(rs.getInt("id"), rs.getString("name"), rs.getString("description"))
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ProjectSummary> getProjectList() {
		return dbList(SQL.PROJECT_LIST) { readProjectSummary(it) }
	}
	
	@Override
	@Transactional(readOnly = true)
	public ProjectSummary getProject(int id) {
		return dbLoad(SQL.PROJECT, id) {readProjectSummary(it) }
	}

	@Override
	@Transactional
	public ProjectSummary createProject(ProjectCreationForm form) {
		// Validation
		validate(form, NameDescription.class);
		// Query
		int id = dbCreate (SQL.PROJECT_CREATE, ["name": form.name, "description": form.description])
		// Audit
		event(Event.of(EventType.PROJECT_CREATED).withProject(id))
		// OK
		new ProjectSummary(id, form.name, form.description)
	}
	
	@Override
	@Transactional
	public Ack deleteProject(int id) {
		def name = getEntityName(Entity.PROJECT, id)
		def ack = dbDelete(SQL.PROJECT_DELETE, id)
		if (ack.success) {
			event(Event.of(EventType.PROJECT_DELETED).withValue("project", name))
		}
		return ack
	}
	
	@Override
	@Transactional(readOnly = true)
	public int getEntityId(Entity entity, String name) {
		def sql = "SELECT ID FROM ${entity.name()} WHERE ${entity.nameColumn()} = :name"
		Integer id = getFirstItem(sql, params("name", name), Integer.class)
		if (id == null) {
			throw new EntityNameNotFoundException (entity, name)
		} else {
			return id
		}
	}
	
	protected String getEntityName (Entity entity, int id) {
		def sql = "SELECT ${entity.nameColumn()} FROM ${entity.name()} WHERE ID = :id"
		String name = getFirstItem(sql, params("id", id), String.class)
		if (name == null) {
			throw new EntityIdNotFoundException (entity, id)
		} else {
			return name
		}
	}
}
