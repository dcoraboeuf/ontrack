package net.ontrack.core.ui;

import java.util.List;

import net.ontrack.core.model.Ack;
import net.ontrack.core.model.BranchCreationForm;
import net.ontrack.core.model.BranchSummary;
import net.ontrack.core.model.ProjectCreationForm;
import net.ontrack.core.model.ProjectGroupCreationForm;
import net.ontrack.core.model.ProjectGroupSummary;
import net.ontrack.core.model.ProjectSummary;

public interface ManageUI {
	
	// Project groups

	List<ProjectGroupSummary> getProjectGroupList();

	ProjectGroupSummary createProjectGroup(ProjectGroupCreationForm form);
	
	// Projects

	List<ProjectSummary> getProjectList();

	ProjectSummary createProject(ProjectCreationForm form);

	ProjectSummary getProject(String idOrName);

	Ack deleteProject(String idOrName);
	
	// Branches

	List<BranchSummary> getBranchList(String projectIdOrName);

	BranchSummary getBranch(String project, String name);

	BranchSummary getBranch(int id);
	
	BranchSummary createBranch (String projectIdOrName, BranchCreationForm form);
	

}
