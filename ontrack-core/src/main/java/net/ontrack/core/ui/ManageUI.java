package net.ontrack.core.ui;

import java.util.List;

import net.ontrack.core.model.Ack;
import net.ontrack.core.model.BranchCreationForm;
import net.ontrack.core.model.BranchSummary;
import net.ontrack.core.model.BuildSummary;
import net.ontrack.core.model.ProjectCreationForm;
import net.ontrack.core.model.ProjectGroupCreationForm;
import net.ontrack.core.model.ProjectGroupSummary;
import net.ontrack.core.model.ProjectSummary;
import net.ontrack.core.model.ValidationStampCreationForm;
import net.ontrack.core.model.ValidationStampSummary;

public interface ManageUI {
	
	// Project groups

	List<ProjectGroupSummary> getProjectGroupList();

	ProjectGroupSummary createProjectGroup(ProjectGroupCreationForm form);
	
	// Projects

	List<ProjectSummary> getProjectList();

	ProjectSummary createProject(ProjectCreationForm form);

	ProjectSummary getProject(String name);

	Ack deleteProject(String name);
	
	// Branches

	List<BranchSummary> getBranchList(String project);

	BranchSummary getBranch(String project, String name);
	
	BranchSummary createBranch (String project, BranchCreationForm form);
	
	// Validation stamps

	List<ValidationStampSummary> getValidationStampList(String project, String branch);

	ValidationStampSummary getValidationStamp(String project, String branch, String name);
	
	ValidationStampSummary createValidationStamp (String project, String branch, ValidationStampCreationForm form);
	
	// Builds

	List<BuildSummary> getBuildList(String project, String branch);

	BuildSummary getBuild(String project, String branch, String name);	

}
