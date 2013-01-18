package net.ontrack.service;

import net.ontrack.core.model.ProjectGroupCreationForm;
import net.ontrack.core.model.ProjectGroupSummary;

public interface ManagementService {

	ProjectGroupSummary createProjectGroup(ProjectGroupCreationForm form);

}
