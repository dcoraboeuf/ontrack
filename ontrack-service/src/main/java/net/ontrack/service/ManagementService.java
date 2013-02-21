package net.ontrack.service;

import net.ontrack.core.model.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ManagementService {
	
	// Project groups

	ProjectGroupSummary createProjectGroup(ProjectGroupCreationForm form);

	List getProjectGroupList();
	
	// Projects

	ProjectSummary createProject(ProjectCreationForm form);

	List<ProjectSummary> getProjectList();

	ProjectSummary getProject(int id);

	Ack deleteProject(int id);
	
	// Branches

	List<BranchSummary> getBranchList(int project);

	BranchSummary getBranch(int id);

	BranchSummary createBranch(int project, BranchCreationForm form);

    Ack deleteBranch(int branchId);
	
	// Validation stamps

	List<ValidationStampSummary> getValidationStampList(int branch);

	ValidationStampSummary getValidationStamp(int id);

	ValidationStampSummary createValidationStamp(int branch, ValidationStampCreationForm form);

	Ack imageValidationStamp(int validationStampId, MultipartFile image);

	byte[] imageValidationStamp(int validationStampId);
	
	// Builds

    BranchBuilds getBuildList(int branchId, int offset, int count);

	BuildSummary getBuild(int buildId);

    List<BuildValidationStamp> getBuildValidationStamps(int buildId);
	
	// Validation runs
	
	ValidationRunSummary getValidationRun (int validationRunId);

    Ack addValidationRunComment(int runId, ValidationRunCommentCreationForm form);

    ValidationRunStatusSummary createValidationRunStatus(int validationRun, ValidationRunStatusCreationForm validationRunStatus, boolean initialStatus);

    // Comments

    CommentStub createComment (Entity entity, int id, String content);
	
	// Common

	int getEntityId(Entity entity, String name, Map<Entity, Integer> parentIds);
}
