package net.ontrack.core.ui;

import java.util.List;

import net.ontrack.core.model.BuildDefinitionForm;
import net.ontrack.core.model.GroupSummaryWithProjects;
import net.ontrack.core.model.ID;
import net.ontrack.core.model.ProjectBranchDetail;
import net.ontrack.core.model.ProjectSummaryWithGroup;
import net.ontrack.core.model.ValidationRunDefinitionForm;

/**
 * This UI is used by clients that will run builds on existing project branches.
 * It needs to access to the list of existing features.
 * 
 */
public interface ControlUI {
	
	/**
	 * List of groups and their projects
	 */
	List<GroupSummaryWithProjects> getGroups ();
	
	/**
	 * List of projects with their group
	 */
	List<ProjectSummaryWithGroup> getProjects();
	
	/**
	 * List of branches with their promotion levels and associated validation stamps
	 */
	ProjectBranchDetail getProjectBranchDetails(int project);
	
	/**
	 * Defines a build
	 */
	ID createBuild (int branch, BuildDefinitionForm build);
	
	/**
	 * Defines a validation run
	 */
	ID createValidationRun (int build, int validationStamp, ValidationRunDefinitionForm validationRun);

}
