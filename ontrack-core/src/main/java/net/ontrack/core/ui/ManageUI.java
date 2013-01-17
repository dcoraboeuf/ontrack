package net.ontrack.core.ui;

import net.ontrack.core.model.ProjectGroupCreationForm;
import net.ontrack.core.model.ProjectGroupSummary;

public interface ManageUI {

	ProjectGroupSummary createProjectGroup(ProjectGroupCreationForm form);

}
