package net.ontrack.core.ui;

import java.util.List;

import net.ontrack.core.model.*;

import org.springframework.web.multipart.MultipartFile;

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

	Ack setImageValidationStamp(String project, String branch, String name, MultipartFile image);

    byte[] imageValidationStamp(String project, String branch, String name);
	
	// Builds

	List<BuildSummary> getBuildList(String project, String branch);

	BuildSummary getBuild(String project, String branch, String name);

    List<BuildValidationStamp> getBuildValidationStamps(String project, String branch, String name);

    // Validation runs

    ValidationRunSummary getValidationRun(int runId);
}
