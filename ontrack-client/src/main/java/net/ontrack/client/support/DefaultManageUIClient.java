package net.ontrack.client.support;

import net.ontrack.client.ManageUIClient;
import net.ontrack.core.model.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Locale;

import static java.lang.String.format;

public class DefaultManageUIClient extends AbstractClient implements ManageUIClient {

    public DefaultManageUIClient(String url) {
        super(url);
    }

    @Override
    public List<ProjectGroupSummary> getProjectGroupList() {
        // FIXME Implement net.ontrack.client.support.DefaultManageUIClient.getProjectGroupList
        return null;
    }

    @Override
    public ProjectGroupSummary createProjectGroup(ProjectGroupCreationForm form) {
        // FIXME Implement net.ontrack.client.support.DefaultManageUIClient.createProjectGroup
        return null;
    }

    @Override
    public String getProjectURL(String project) {
        return getUrl(format("/gui/project/%s", project));
    }

    @Override
    public String getBranchURL(String project, String branch) {
        return getUrl(format("/gui/project/%s/branch/%s", project, branch));
    }

    @Override
    public String getPromotionLevelImageURL(String project, String branch, String name) {
        return getUrl(format("/ui/manage/project/%s/branch/%s/promotion_level/%s/image", project, branch, name));
    }

    @Override
    public String getValidationStampImageURL(String project, String branch, String name) {
        return getUrl(format("/ui/manage/project/%s/branch/%s/validation_stamp/%s/image", project, branch, name));
    }

    @Override
    public List<ProjectSummary> getProjectList() {
        return list("/ui/manage/project", ProjectSummary.class);
    }

    @Override
    public ProjectSummary createProject(ProjectCreationForm form) {
        // FIXME Implement net.ontrack.client.support.DefaultManageUIClient.createProject
        return null;
    }

    @Override
    public ProjectSummary getProject(String name) {
        return get(format("/ui/manage/project/%s", name), ProjectSummary.class);
    }

    @Override
    public Ack deleteProject(String name) {
        // FIXME Implement net.ontrack.client.support.DefaultManageUIClient.deleteProject
        return null;
    }

    @Override
    public ProjectSummary updateProject(String name, ProjectUpdateForm form) {
        return put(format("/ui/manage/project/%s", name), ProjectSummary.class, form);
    }

    @Override
    public List<BranchSummary> getBranchList(String project) {
        return list(format("/ui/manage/project/%s/branch", project), BranchSummary.class);
    }

    @Override
    public BranchSummary getBranch(String project, String name) {
        return get(format("/ui/manage/project/%s/branch/%s", project, name), BranchSummary.class);
    }

    @Override
    public BranchFilterData getBranchFilterData(String project, String branch) {
        return get(format("/ui/manage/project/%s/branch/%s/filter", project, branch), BranchFilterData.class);
    }

    @Override
    public BranchSummary createBranch(String project, BranchCreationForm form) {
        // FIXME Implement net.ontrack.client.support.DefaultManageUIClient.createBranch
        return null;
    }

    @Override
    public Ack deleteBranch(String project, String name) {
        // FIXME Implement net.ontrack.client.support.DefaultManageUIClient.deleteBranch
        return null;
    }

    @Override
    public List<ValidationStampSummary> getValidationStampList(String project, String branch) {
        return list(format("/ui/manage/project/%s/branch/%s/validation_stamp", project, branch), ValidationStampSummary.class);
    }

    @Override
    public ValidationStampSummary getValidationStamp(String project, String branch, String name) {
        // FIXME Implement net.ontrack.client.support.DefaultManageUIClient.getValidationStamp
        return null;
    }

    @Override
    public ValidationStampSummary createValidationStamp(String project, String branch, ValidationStampCreationForm form) {
        // FIXME Implement net.ontrack.client.support.DefaultManageUIClient.createValidationStamp
        return null;
    }

    @Override
    public Ack deleteValidationStamp(String project, String branch, String validationStamp) {
        // FIXME Implement net.ontrack.client.support.DefaultManageUIClient.deleteValidationStamp
        return null;
    }

    @Override
    public Ack setImageValidationStamp(String project, String branch, String name, MultipartFile image) {
        // FIXME Implement net.ontrack.client.support.DefaultManageUIClient.setImageValidationStamp
        return null;
    }

    @Override
    public byte[] imageValidationStamp(String project, String branch, String name) {
        // FIXME Implement net.ontrack.client.support.DefaultManageUIClient.imageValidationStamp
        return new byte[0];
    }

    @Override
    public Ack linkValidationStampToPromotionLevel(String project, String branch, String validationStamp, String promotionLevel) {
        // FIXME Implement net.ontrack.client.support.DefaultManageUIClient.linkValidationStampToPromotionLevel
        return null;
    }

    @Override
    public Ack unlinkValidationStampToPromotionLevel(String project, String branch, String validationStamp) {
        // FIXME Implement net.ontrack.client.support.DefaultManageUIClient.unlinkValidationStampToPromotionLevel
        return null;
    }

    @Override
    public PromotionLevelSummary getPromotionLevel(String project, String branch, String name) {
        // FIXME Implement net.ontrack.client.support.DefaultManageUIClient.getPromotionLevel
        return null;
    }

    @Override
    public PromotionLevelSummary createPromotionLevel(String project, String branch, PromotionLevelCreationForm form) {
        // FIXME Implement net.ontrack.client.support.DefaultManageUIClient.createPromotionLevel
        return null;
    }

    @Override
    public Ack deletePromotionLevel(String project, String branch, String name) {
        return delete(format("/ui/manage/project/%s/branch/%s/promotion_level/%s", project, branch, name), Ack.class);
    }

    @Override
    public Ack setImagePromotionLevel(String project, String branch, String name, MultipartFile image) {
        // FIXME Implement net.ontrack.client.support.DefaultManageUIClient.setImagePromotionLevel
        return null;
    }

    @Override
    public byte[] imagePromotionLevel(String project, String branch, String name) {
        // FIXME Implement net.ontrack.client.support.DefaultManageUIClient.imagePromotionLevel
        return new byte[0];
    }

    @Override
    public List<PromotionLevelSummary> getPromotionLevelList(String project, String branch) {
        return list(format("/ui/manage/project/%s/branch/%s/promotion_level", project, branch), PromotionLevelSummary.class);
    }

    @Override
    public Ack upPromotionLevel(String project, String branch, String promotionLevel) {
        // FIXME Implement net.ontrack.client.support.DefaultManageUIClient.upPromotionLevel
        return null;
    }

    @Override
    public Ack downPromotionLevel(String project, String branch, String promotionLevel) {
        // FIXME Implement net.ontrack.client.support.DefaultManageUIClient.downPromotionLevel
        return null;
    }

    @Override
    public PromotionLevelManagementData getPromotionLevelManagementData(String project, String branch) {
        // FIXME Implement net.ontrack.client.support.DefaultManageUIClient.getPromotionLevelManagementData
        return null;
    }

    @Override
    public BranchBuilds getBuilds(Locale locale, String project, String branch, BuildFilter filter) {
        // TODO Locale management
        return post(format("/ui/manage/project/%s/branch/%s/query", project, branch), BranchBuilds.class, filter);
    }

    @Override
    public BuildSummary getBuild(String project, String branch, String name) {
        return get(format("/ui/manage/project/%s/branch/%s/build/%s", project, branch, name), BuildSummary.class);
    }

    @Override
    public BuildSummary getLastBuild(String project, String branch) {
        return get(format("/ui/manage/project/%s/branch/%s/build/last", project, branch), BuildSummary.class);
    }

    @Override
    public BuildSummary getLastBuildWithValidationStamp(Locale locale, String project, String branch, String validationStamp) {
        return get(format("/ui/manage/project/%s/branch/%s/build/withValidationStamp/%s", project, branch, validationStamp), BuildSummary.class);
    }

    @Override
    public BuildSummary getLastBuildWithPromotionLevel(Locale locale, String project, String branch, String promotionLevel) {
        return get(format("/ui/manage/project/%s/branch/%s/build/withPromotionLevel/%s", project, branch, promotionLevel), BuildSummary.class);
    }

    @Override
    public List<BuildValidationStamp> getBuildValidationStamps(Locale locale, String project, String branch, String name) {
        // TODO Locale management
        return list(format("/ui/manage/project/%s/branch/%s/build/%s/validationStamps", project, branch, name), BuildValidationStamp.class);
    }

    @Override
    public List<BuildPromotionLevel> getBuildPromotionLevels(Locale locale, String project, String branch, String name) {
        // TODO Locale management
        return list(format("/ui/manage/project/%s/branch/%s/build/%s/promotionLevels", project, branch, name), BuildPromotionLevel.class);
    }

    @Override
    public ValidationRunSummary getValidationRun(String project, String branch, String build, String validationStamp, int run) {
        // FIXME Implement net.ontrack.client.support.DefaultManageUIClient.getValidationRun
        return null;
    }

    @Override
    public List<ValidationRunEvent> getValidationRunHistory(Locale locale, int validationRunId, int offset, int count) {
        // TODO Locale management
        return list(format("/ui/manage/validation_run/%d/history?offset=%d&count=%d", validationRunId, offset, count), ValidationRunEvent.class);
    }

    @Override
    public Ack addValidationRunComment(int runId, ValidationRunCommentCreationForm form) {
        // FIXME Implement net.ontrack.client.support.DefaultManageUIClient.addValidationRunComment
        return null;
    }

}
