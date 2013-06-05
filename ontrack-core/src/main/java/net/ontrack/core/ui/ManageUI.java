package net.ontrack.core.ui;

import net.ontrack.core.model.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

public interface ManageUI {

    // Projects

    List<ProjectSummary> getProjectList();

    ProjectSummary createProject(ProjectCreationForm form);

    ProjectSummary getProject(String name);

    Ack deleteProject(String name);

    ProjectSummary updateProject(String name, ProjectUpdateForm form);

    // Branches

    List<BranchSummary> getBranchList(String project);

    BranchSummary getBranch(String project, String name);

    BranchCloneInfo getBranchCloneInfo(Locale locale, String project, String name);

    DecoratedBranch getDecoratedBranch(Locale locale, String project, String name);

    BranchSummary createBranch(String project, BranchCreationForm form);

    BranchSummary updateBranch(String project, String name, BranchUpdateForm form);

    BranchSummary cloneBranch(String project, String name, BranchCloneForm form);

    Ack deleteBranch(String project, String name);

    BranchFilterData getBranchFilterData(String project, String branch);

    BuildCleanup getBuildCleanup(String project, String branch);

    Ack setBuildCleanup(String project, String branch, BuildCleanupForm form);

    // Validation stamps

    List<ValidationStampSummary> getValidationStampList(String project, String branch);

    ValidationStampSummary getValidationStamp(String project, String branch, String name);

    DecoratedValidationStamp getDecoratedValidationStamp(Locale locale, String project, String branch, String validationStamp);

    ValidationStampSummary createValidationStamp(String project, String branch, ValidationStampCreationForm form);

    ValidationStampSummary updateValidationStamp(String project, String branch, String validationStamp, ValidationStampUpdateForm form);

    Ack deleteValidationStamp(String project, String branch, String validationStamp);

    Ack setImageValidationStamp(String project, String branch, String name, MultipartFile image);

    byte[] imageValidationStamp(String project, String branch, String name);

    Ack linkValidationStampToPromotionLevel (String project, String branch, String validationStamp, String promotionLevel);

    Ack unlinkValidationStampToPromotionLevel (String project, String branch, String validationStamp);

    Ack upValidationStamp (String project, String branch, String validationStamp);

    Ack downValidationStamp (String project, String branch, String validationStamp);

    Ack setValidationStampOwner (String project, String branch, String validationStamp, int ownerId);

    Ack unsetValidationStampOwner (String project, String branch, String validationStamp);

    Ack addValidationStampComment(String project, String branch, String validationStamp, ValidationStampCommentForm form);

    Collection<Comment> getValidationStampComments(Locale locale, String project, String branch, String validationStamp, int offset, int count);

    // Promotion levels

    PromotionLevelSummary getPromotionLevel(String project, String branch, String name);

    PromotionLevelSummary createPromotionLevel(String project, String branch, PromotionLevelCreationForm form);

    PromotionLevelSummary updatePromotionLevel(String project, String branch, String promotionLevel, PromotionLevelUpdateForm form);

    Ack deletePromotionLevel(String project, String branch, String name);

    Ack setImagePromotionLevel(String project, String branch, String name, MultipartFile image);

    byte[] imagePromotionLevel(String project, String branch, String name);

    List<PromotionLevelSummary> getPromotionLevelList(String project, String branch);

    Ack upPromotionLevel(String project, String branch, String promotionLevel);

    Ack downPromotionLevel(String project, String branch, String promotionLevel);

    PromotionLevelManagementData getPromotionLevelManagementData (String project, String branch);

    Flag setPromotionLevelAutoPromote(String project, String branch, String promotionLevel);

    Flag unsetPromotionLevelAutoPromote(String project, String branch, String promotionLevel);

    // Builds

    BuildSummary getBuild(String project, String branch, String name);

    BuildSummary getLastBuild(String project, String branch);

    BuildSummary getLastBuildWithValidationStamp(Locale locale, String project, String branch, String validationStamp);

	BuildSummary getLastBuildWithPromotionLevel(Locale locale, String project, String branch, String promotionLevel);

    List<BuildValidationStamp> getBuildValidationStamps(Locale locale, String project, String branch, String name);

    List<BuildPromotionLevel> getBuildPromotionLevels(Locale locale, String project, String branch, String name);

    BranchBuilds getBuilds(Locale locale, String project, String branch, BuildFilter filter);

    Ack deleteBuild(String project, String branch, String build);

    // Validation runs

    List<ValidationRunEvent> getValidationRunHistory(Locale locale, int validationRunId, int offset, int count);

    List<ValidationRunEvent> getValidationRunsForValidationStamp(Locale locale, String project, String branch, String validationStamp, int offset, int count);

    ValidationRunSummary getValidationRun(String project, String branch, String build, String validationStamp, int run);

    Ack addValidationRunComment(int runId, ValidationRunCommentCreationForm form);

    // Promoted runs

    List<Promotion> getPromotions(Locale locale, String project, String branch, String promotionLevel, int offset, int count);

    Ack removePromotedRun(String project, String branch, String build, String promotionLevel);
}
