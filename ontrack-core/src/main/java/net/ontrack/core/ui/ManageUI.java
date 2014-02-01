package net.ontrack.core.ui;

import net.ontrack.core.model.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

public interface ManageUI {

    // Gets the current version

    String getVersion();

    // Projects

    List<ProjectSummary> getProjectList();

    ProjectSummary createProject(ProjectCreationForm form);

    ProjectSummary getProject(String name);

    Ack deleteProject(String name);

    ProjectSummary updateProject(String name, ProjectUpdateForm form);

    Ack updateProjectValidationStamps(String project, ProjectValidationStampMgt form);

    // Project I/O

    ExportData backupSave() throws Exception;

    ImportResult backupRestore(MultipartFile file) throws Exception;

    ExportResponse exportLaunch(ExportForm form);

    ExportResponse exportProjectLaunch(String project);

    Ack exportProjectCheck(String uuid);

    ExportData exportProjectDownload(String uuid);

    ImportResponse importLaunch(MultipartFile file);

    ImportResult importCheck(String uuid);

    // Branches

    List<BranchSummary> getBranchAll();

    List<BranchSummary> getBranchList(String project);

    List<BranchLastStatus> getBranchLastStatusList(Locale locale, String project);

    BranchSummary getBranch(String project, String name);

    BranchLastStatus getBranchLastStatus(Locale locale, String project, String name);

    BranchCloneInfo getBranchCloneInfo(Locale locale, String project, String name);

    DecoratedBranch getDecoratedBranch(Locale locale, String project, String name);

    BranchSummary createBranch(String project, BranchCreationForm form);

    BranchSummary updateBranch(String project, String name, BranchUpdateForm form);

    BranchSummary cloneBranch(String project, String name, BranchCloneForm form);

    Ack deleteBranch(String project, String name);

    BranchFilterData getBranchFilterData(Locale locale, String project, String branch);

    BuildCleanup getBuildCleanup(String project, String branch);

    Ack setBuildCleanup(String project, String branch, BuildCleanupForm form);

    // Dashboard management

    List<DashboardConfig> getDashboards();

    DashboardConfig createDashboard(DashboardConfigForm form);

    DashboardConfig getDashboard(int id);

    DashboardConfig updateDashboard(int id, DashboardConfigForm form);

    Ack deleteDashboard(int id);

    // Validation stamps

    List<ValidationStampSummary> getValidationStampList(String project, String branch);

    ValidationStampSummary getValidationStamp(String project, String branch, String name);

    DecoratedValidationStamp getDecoratedValidationStamp(Locale locale, String project, String branch, String validationStamp);

    ValidationStampSummary createValidationStamp(String project, String branch, ValidationStampCreationForm form);

    ValidationStampSummary updateValidationStamp(String project, String branch, String validationStamp, ValidationStampUpdateForm form);

    Ack deleteValidationStamp(String project, String branch, String validationStamp);

    Ack setImageValidationStamp(String project, String branch, String name, MultipartFile image);

    byte[] imageValidationStamp(String project, String branch, String name);

    Ack linkValidationStampToPromotionLevel(String project, String branch, String validationStamp, String promotionLevel);

    Ack unlinkValidationStampToPromotionLevel(String project, String branch, String validationStamp);

    Ack upValidationStamp(String project, String branch, String validationStamp);

    Ack downValidationStamp(String project, String branch, String validationStamp);

    Ack moveValidationStamp(String project, String branch, String validationStamp, Reordering reordering);

    Ack setValidationStampOwner(String project, String branch, String validationStamp, int ownerId);

    Ack unsetValidationStampOwner(String project, String branch, String validationStamp);

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

    PromotionLevelManagementData getPromotionLevelManagementData(String project, String branch);

    PromotionLevelAndStamps getPromotionLevelValidationStamps(String project, String branch, String promotionLevel);

    Flag setPromotionLevelAutoPromote(String project, String branch, String promotionLevel);

    Flag unsetPromotionLevelAutoPromote(String project, String branch, String promotionLevel);

    // Builds

    BuildSummary getBuild(String project, String branch, String name);

    BuildSummary updateBuild(String project, String branch, String build, BranchUpdateForm form);

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

    Ack deleteValidationRun(String project, String branch, String build, String validationStamp, int runOrder);

    // Promoted runs

    List<Promotion> getPromotions(Locale locale, String project, String branch, String promotionLevel, int offset, int count);

    Ack removePromotedRun(String project, String branch, String build, String promotionLevel);
}
