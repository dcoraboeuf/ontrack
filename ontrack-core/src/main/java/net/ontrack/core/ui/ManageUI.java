package net.ontrack.core.ui;

import java.util.List;

import net.ontrack.core.model.ProjectGroupCreationForm;
import net.ontrack.core.model.ProjectGroupSummary;

public interface ManageUI {

	List<ProjectGroupSummary> getProjectGroupList();

	ProjectGroupSummary createProjectGroup(ProjectGroupCreationForm form);

}
