package net.ontrack.service;

import net.ontrack.core.model.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

public interface ManagementService {
	
	// Projects

	ProjectSummary createProject(ProjectCreationForm form);

	List<ProjectSummary> getProjectList();

	ProjectSummary getProject(int id);

	Ack deleteProject(int id);

    ProjectSummary updateProject(int id, ProjectUpdateForm form);
	
	// Branches

	List<BranchSummary> getBranchList(int project);

	BranchSummary getBranch(int id);

    DecoratedBranch getDecoratedBranch(Locale locale, int branchId);

	BranchSummary createBranch(int project, BranchCreationForm form);

    Ack deleteBranch(int branchId);

    BranchSummary updateBranch(int branch, BranchUpdateForm form);

    BranchSummary cloneBranch(int branchId, BranchCloneForm form);
	
	// Validation stamps

	List<ValidationStampSummary> getValidationStampList(int branch);

	ValidationStampSummary getValidationStamp(int id);

    DecoratedValidationStamp getDecoratedValidationStamp(Locale locale, int validationStampId);

    ValidationStampSummary createValidationStamp(int branch, ValidationStampCreationForm form);

    ValidationStampSummary updateValidationStamp(int validationStampId, ValidationStampUpdateForm form);

	Ack imageValidationStamp(int validationStampId, MultipartFile image);

	byte[] imageValidationStamp(int validationStampId);

    Ack linkValidationStampToPromotionLevel(int validationStampId, int promotionLevelId);

    Ack unlinkValidationStampToPromotionLevel(int validationStampId);

    Ack deleteValidationStamp(int validationStampId);

    Ack upValidationStamp(int validationStampId);

    Ack downValidationStamp(int validationStampId);

    Ack setValidationStampOwner(int validationStampId, int ownerId);

    Ack unsetValidationStampOwner(int validationStampId);

    Ack addValidationStampComment(int validationStampId, ValidationStampCommentForm form);

    Collection<Comment> getValidationStampComments (Locale locale, int validationStampId, int offset, int count);

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

    Flag setPromotionLevelAutoPromote(int promotionLevelId);

    Flag unsetPromotionLevelAutoPromote(int promotionLevelId);

    PromotionLevelSummary getPromotionLevelForValidationStamp(int validationStamp);

    boolean isPromotionLevelComplete(int build, int promotionLevel);
	
	// Builds

    BranchBuilds getBuildList(Locale locale, int branchId, int offset, int count);

    BranchBuilds queryBuilds(Locale locale, int branch, BuildFilter filter);

	BuildSummary findLastBuildWithValidationStamp(int validationStampId, Set<Status> statuses);

	BuildSummary findLastBuildWithPromotionLevel(int promotionLevelId);

    BuildSummary getLastBuild(int branch);

    Integer findBuildByName(int branchId, String buildName);

    /**
     * Finds a build on this branch whose name is the closest. It assumes that build names
     * are in a numeric format.
     */
    Integer findBuildAfterUsingNumericForm(int branchId, String buildName);

	BuildSummary getBuild(int build);

    List<BuildValidationStamp> getBuildValidationStamps(Locale locale, int buildId);

    List<BuildPromotionLevel> getBuildPromotionLevels(Locale locale, int buildId);
	
	// Validation runs
	
	ValidationRunSummary getValidationRun (int validationRunId);

    Ack addValidationRunComment(int runId, ValidationRunCommentCreationForm form);

    ValidationRunStatusSummary createValidationRunStatus(int validationRun, ValidationRunStatusCreationForm validationRunStatus, boolean initialStatus);

    List<BuildValidationStampRun> getValidationRuns(Locale locale, int buildId, int validationStampId);

    List<ValidationRunEvent> getValidationRunHistory(Locale locale, int validationRunId, int offset, int count);

    List<ValidationRunEvent> getValidationRunsForValidationStamp(Locale locale, int validationStampId, int offset, int count);

    List<ValidationRunStatusStub> getStatusesForLastBuilds(int validationStampId, int count);

    // Promoted runs

    PromotedRunSummary getPromotedRun(int buildId, int promotionLevel);

    Promotion getEarliestPromotionForBuild(Locale locale, int buildId, int promotionLevelId);

    Promotion findLastPromotion(Locale locale, int promotionLevelId);

    // Comments

    CommentStub createComment (Entity entity, int id, String content);
	
	// Common

	int getEntityId(Entity entity, String name, Map<Entity, Integer> parentIds);
}
