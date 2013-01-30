package net.ontrack.service;

import java.util.List;

import net.ontrack.core.model.Ack;
import net.ontrack.core.model.ProjectCreationForm;
import net.ontrack.core.model.ProjectGroupCreationForm;
import net.ontrack.core.model.ProjectGroupSummary;
import net.ontrack.core.model.ProjectSummary;

public interface ManagementService {
	
	// Project groups

	ProjectGroupSummary createProjectGroup(ProjectGroupCreationForm form);

	List<ProjectGroupSummary> getProjectGroupList();
	
	// Projects

	ProjectSummary createProject(ProjectCreationForm form);

	List<ProjectSummary> getProjectList();

	ProjectSummary getProject(int id);

	Ack deleteProject(int id);

}
