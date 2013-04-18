package net.ontrack.service;

import net.ontrack.core.model.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public interface ManagementService {
	
	// Project groups

	ProjectGroupSummary createProjectGroup(ProjectGroupCreationForm form);

	List<ProjectGroupSummary> getProjectGroupList();
	
	// Projects

	ProjectSummary createProject(ProjectCreationForm form);

	List<ProjectSummary> getProjectList();

	ProjectSummary getProject(int id);

	Ack deleteProject(int id);

    ProjectSummary updateProject(int id, ProjectUpdateForm form);
	
	// Branches

	List<BranchSummary> getBranchList(int project);

	BranchSummary getBranch(int id);

	BranchSummary createBranch(int project, BranchCreationForm form);

    Ack deleteBranch(int branchId);

    BranchSummary updateBranch(int branch, BranchUpdateForm form);

    BranchSummary cloneBranch(int branchId, BranchCloneForm form);
	
	// Validation stamps

	List<ValidationStampSummary> getValidationStampList(int branch);

	ValidationStampSummary getValidationStamp(int id);

    ValidationStampSummary createValidationStamp(int branch, ValidationStampCreationForm form);

    ValidationStampSummary updateValidationStamp(int validationStampId, ValidationStampUpdateForm form);

	Ack imageValidationStamp(int validationStampId, MultipartFile image);

	byte[] imageValidationStamp(int validationStampId);

    Ack linkValidationStampToPromotionLevel(int validationStampId, int promotionLevelId);

    Ack unlinkValidationStampToPromotionLevel(int validationStampId);

    Ack deleteValidationStamp(int validationStampId);

    // Promotion levels

    List<PromotionLevelSummary> getPromotionLevelList(int branchId);

    PromotionLevelSummary getPromotionLevel(int promotionLevelId);

    PromotionLevelSummary createPromotionLevel(int branchId, PromotionLevelCreationForm form);

    PromotionLevelSummary updatePromotionLevel(int promotionLevelId, PromotionLevelUpdateForm form);

    Ack deletePromotionLevel(int promotionLevelId);

    Ack imagePromotionLevel(int promotionLevelId, MultipartFile image);

    byte[] imagePromotionLevel(int promotionLevelId);

    Ack upPromotionLevel(int promotionLevelId);

    Ack downPromotionLevel(int promotionLevelId);

    PromotionLevelManagementData getPromotionLevelManagementData(int branchId);
	
	// Builds

    BranchBuilds getBuildList(Locale locale, int branchId, int offset, int count);

    BranchBuilds queryBuilds(Locale locale, int branch, BuildFilter filter);

	BuildSummary queryLastBuildWithValidationStamp(Locale locale, int branch, String validationStamp, Set<Status> statuses);

	BuildSummary queryLastBuildWithPromotionLevel(Locale locale, int branch, String promotionLevel);

    BuildSummary getLastBuild(int branch);

	BuildSummary getBuild(int build);

    List<BuildValidationStamp> getBuildValidationStamps(Locale locale, int buildId);

    List<BuildPromotionLevel> getBuildPromotionLevels(Locale locale, int buildId);
	
	// Validation runs
	
	ValidationRunSummary getValidationRun (int validationRunId);

    Ack addValidationRunComment(int runId, ValidationRunCommentCreationForm form);

    ValidationRunStatusSummary createValidationRunStatus(int validationRun, ValidationRunStatusCreationForm validationRunStatus, boolean initialStatus);

    List<BuildValidationStampRun> getValidationRuns(Locale locale, int buildId, int validationStampId);

    List<ValidationRunEvent> getValidationRunHistory(Locale locale, int validationRunId, int offset, int count);

    // Promoted runs

    PromotedRunSummary getPromotedRun(int buildId, int promotionLevel);

    // Comments

    CommentStub createComment (Entity entity, int id, String content);
	
	// Common

	int getEntityId(Entity entity, String name, Map<Entity, Integer> parentIds);
}
