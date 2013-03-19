package net.ontrack.core.ui;

import net.ontrack.core.model.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Locale;

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

    BranchSummary createBranch(String project, BranchCreationForm form);

    Ack deleteBranch(String project, String name);

    // Validation stamps

    List<ValidationStampSummary> getValidationStampList(String project, String branch);

    ValidationStampSummary getValidationStamp(String project, String branch, String name);

    ValidationStampSummary createValidationStamp(String project, String branch, ValidationStampCreationForm form);

    Ack deleteValidationStamp(String project, String branch, String validationStamp);

    Ack setImageValidationStamp(String project, String branch, String name, MultipartFile image);

    byte[] imageValidationStamp(String project, String branch, String name);

    Ack linkValidationStampToPromotionLevel (String project, String branch, String validationStamp, String promotionLevel);

    Ack unlinkValidationStampToPromotionLevel (String project, String branch, String validationStamp);

    // Promotion levels

    PromotionLevelSummary getPromotionLevel(String project, String branch, String name);

    PromotionLevelSummary createPromotionLevel(String project, String branch, PromotionLevelCreationForm form);

    Ack setImagePromotionLevel(String project, String branch, String name, MultipartFile image);

    byte[] imagePromotionLevel(String project, String branch, String name);

    List<PromotionLevelSummary> getPromotionLevelList(String project, String branch);

    Ack upPromotionLevel(String project, String branch, String promotionLevel);

    Ack downPromotionLevel(String project, String branch, String promotionLevel);

    PromotionLevelManagementData getPromotionLevelManagementData (String project, String branch);

    // Builds

    BranchBuilds getBuildList(Locale locale, String project, String branch, int offset, int count);

    BuildSummary getBuild(String project, String branch, String name);

    BuildSummary getLastBuild(String project, String branch);

    BuildSummary getLastBuildWithValidationStamp(Locale locale, String project, String branch, String validationStamp);

	BuildSummary getLastBuildWithPromotionLevel(Locale locale, String project, String branch, String promotionLevel);

    List<BuildValidationStamp> getBuildValidationStamps(Locale locale, String project, String branch, String name);

    List<BuildPromotionLevel> getBuildPromotionLevels(Locale locale, String project, String branch, String name);

    BranchBuilds queryBuilds(Locale locale, String project, String branch, BuildFilter filter);

    // Validation runs

    ValidationRunSummary getValidationRun(String project, String branch, String build, String validationStamp, int run);

    Ack addValidationRunComment(int runId, ValidationRunCommentCreationForm form);
}
