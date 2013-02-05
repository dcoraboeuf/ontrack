package net.ontrack.service;

import java.util.List;
import java.util.Map;

import net.ontrack.core.model.*;
import org.springframework.web.multipart.MultipartFile;

public interface ManagementService {
	
	// Project groups

	ProjectGroupSummary createProjectGroup(ProjectGroupCreationForm form);

	List<ProjectGroupSummary> getProjectGroupList();
	
	// Projects

	ProjectSummary createProject(ProjectCreationForm form);

	List<ProjectSummary> getProjectList();

	ProjectSummary getProject(int id);

	Ack deleteProject(int id);
	
	// Branches

	List<BranchSummary> getBranchList(int project);

	BranchSummary getBranch(int id);

	BranchSummary createBranch(int project, BranchCreationForm form);
	
	// Validation stamps

	List<ValidationStampSummary> getValidationStampList(int branch);

	ValidationStampSummary getValidationStamp(int id);

	ValidationStampSummary createValidationStamp(int branch, ValidationStampCreationForm form);

	Ack imageValidationStamp(int validationStampId, MultipartFile image);

	byte[] imageValidationStamp(int validationStampId);
	
	// Builds

	List<BuildSummary> getBuildList(int branchId);

	BuildSummary getBuild(int buildId);

    List<BuildValidationStamp> getBuildValidationStamps(int buildId);
	
	// Validation runs
	
	ValidationRunSummary getValidationRun (int validationRunId);
	
	// Common

	int getEntityId(Entity entity, String name, Map<Entity, Integer> parentIds);

}
