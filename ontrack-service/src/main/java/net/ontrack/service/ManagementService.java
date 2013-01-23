package net.ontrack.service;

import java.util.List;

import net.ontrack.core.model.ProjectGroupCreationForm;
import net.ontrack.core.model.ProjectGroupSummary;

public interface ManagementService {

	ProjectGroupSummary createProjectGroup(ProjectGroupCreationForm form);

	List<ProjectGroupSummary> getProjectGroupList();

}
