package net.ontrack.web.ui;

import net.ontrack.core.model.*;
import net.ontrack.core.ui.ManageUI;
import net.ontrack.service.ManagementService;
import net.ontrack.web.support.EntityConverter;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

@Controller
public class ManageUIController extends AbstractEntityUIController implements ManageUI {

    private final ManagementService managementService;

    @Autowired
    public ManageUIController(ErrorHandler errorHandler, Strings strings, ManagementService managementService, EntityConverter entityConverter) {
        super(errorHandler, strings, entityConverter);
        this.managementService = managementService;
    }

    // Project groups

    @Override
    @RequestMapping(value = "/ui/manage/project_group", method = RequestMethod.GET)
    public
    @ResponseBody
    List<ProjectGroupSummary> getProjectGroupList() {
        return managementService.getProjectGroupList();
    }

    @Override
    @RequestMapping(value = "/ui/manage/project_group", method = RequestMethod.POST)
    public
    @ResponseBody
    ProjectGroupSummary createProjectGroup(@RequestBody ProjectGroupCreationForm form) {
        return managementService.createProjectGroup(form);
    }

    // Projects

    @Override
    @RequestMapping(value = "/ui/manage/project", method = RequestMethod.GET)
    public
    @ResponseBody
    List<ProjectSummary> getProjectList() {
        return managementService.getProjectList();
    }

    @Override
    @RequestMapping(value = "/ui/manage/project", method = RequestMethod.POST)
    public
    @ResponseBody
    ProjectSummary createProject(@RequestBody ProjectCreationForm form) {
        return managementService.createProject(form);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{name:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.GET)
    public
    @ResponseBody
    ProjectSummary getProject(@PathVariable String name) {
        return managementService.getProject(entityConverter.getProjectId(name));
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{name:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    Ack deleteProject(@PathVariable String name) {
        return managementService.deleteProject(entityConverter.getProjectId(name));
    }

    // Branches

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch", method = RequestMethod.GET)
    public
    @ResponseBody
    List<BranchSummary> getBranchList(@PathVariable String project) {
        int projectId = entityConverter.getProjectId(project);
        return managementService.getBranchList(projectId);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{name:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.GET)
    public
    @ResponseBody
    BranchSummary getBranch(@PathVariable String project, @PathVariable String name) {
        int branchId = entityConverter.getBranchId(project, name);
        return managementService.getBranch(branchId);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch", method = RequestMethod.POST)
    public
    @ResponseBody
    BranchSummary createBranch(@PathVariable String project, @RequestBody BranchCreationForm form) {
        int projectId = entityConverter.getProjectId(project);
        return managementService.createBranch(projectId, form);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{name:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    Ack deleteBranch(@PathVariable String project, @PathVariable String name) {
        int branchId = entityConverter.getBranchId(project, name);
        return managementService.deleteBranch(branchId);
    }

    // Validation stamps

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/validation_stamp", method = RequestMethod.GET)
    public
    @ResponseBody
    List<ValidationStampSummary> getValidationStampList(@PathVariable String project, @PathVariable String branch) {
        int branchId = entityConverter.getBranchId(project, branch);
        return managementService.getValidationStampList(branchId);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/validation_stamp/{name:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.GET)
    public
    @ResponseBody
    ValidationStampSummary getValidationStamp(@PathVariable String project, @PathVariable String branch, @PathVariable String name) {
        int validationStampId = entityConverter.getValidationStampId(project, branch, name);
        return managementService.getValidationStamp(validationStampId);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/validation_stamp", method = RequestMethod.POST)
    public
    @ResponseBody
    ValidationStampSummary createValidationStamp(@PathVariable String project, @PathVariable String branch, @RequestBody ValidationStampCreationForm form) {
        int branchId = entityConverter.getBranchId(project, branch);
        return managementService.createValidationStamp(branchId, form);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/validation_stamp/{name:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    Ack deleteValidationStamp(@PathVariable String project, @PathVariable String branch, @PathVariable String name) {
        int validationStampId = entityConverter.getValidationStampId(project, branch, name);
        return managementService.deleteValidationStamp(validationStampId);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/validation_stamp/{name:[A-Za-z0-9_\\.\\-]+}/image", method = RequestMethod.POST)
    public
    @ResponseBody
    Ack setImageValidationStamp(@PathVariable String project, @PathVariable String branch, @PathVariable String name, @RequestParam MultipartFile image) {
        int validationStampId = entityConverter.getValidationStampId(project, branch, name);
        return managementService.imageValidationStamp(validationStampId, image);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/validation_stamp/{name:[A-Za-z0-9_\\.\\-]+}/image", method = RequestMethod.GET)
    public
    @ResponseBody
    byte[] imageValidationStamp(@PathVariable String project, @PathVariable String branch, @PathVariable String name) {
        int validationStampId = entityConverter.getValidationStampId(project, branch, name);
        return managementService.imageValidationStamp(validationStampId);
    }

    // Promotion levels

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/promotion_level", method = RequestMethod.GET)
    public
    @ResponseBody
    List<PromotionLevelSummary> getPromotionLevelList(@PathVariable String project, @PathVariable String branch) {
        int branchId = entityConverter.getBranchId(project, branch);
        return managementService.getPromotionLevelList(branchId);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/promotion_level/{name:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.GET)
    public
    @ResponseBody
    PromotionLevelSummary getPromotionLevel(@PathVariable String project, @PathVariable String branch, @PathVariable String name) {
        int promotionLevelId = entityConverter.getPromotionLevelId(project, branch, name);
        return managementService.getPromotionLevel(promotionLevelId);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/promotion_level", method = RequestMethod.POST)
    public
    @ResponseBody
    PromotionLevelSummary createPromotionLevel(@PathVariable String project, @PathVariable String branch, @RequestBody PromotionLevelCreationForm form) {
        int branchId = entityConverter.getBranchId(project, branch);
        return managementService.createPromotionLevel(branchId, form);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/promotion_level/{name:[A-Za-z0-9_\\.\\-]+}/image", method = RequestMethod.POST)
    public
    @ResponseBody
    Ack setImagePromotionLevel(@PathVariable String project, @PathVariable String branch, @PathVariable String name, @RequestParam MultipartFile image) {
        int promotionLevelId = entityConverter.getPromotionLevelId(project, branch, name);
        return managementService.imagePromotionLevel(promotionLevelId, image);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/promotion_level/{name:[A-Za-z0-9_\\.\\-]+}/image", method = RequestMethod.GET)
    public
    @ResponseBody
    byte[] imagePromotionLevel(@PathVariable String project, @PathVariable String branch, @PathVariable String name) {
        int promotionLevelId = entityConverter.getPromotionLevelId(project, branch, name);
        return managementService.imagePromotionLevel(promotionLevelId);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/validation_stamp/{validationStamp:[A-Za-z0-9_\\.\\-]+}/link/{promotionLevel:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.GET)
    public
    @ResponseBody
    Ack linkValidationStampToPromotionLevel(@PathVariable String project, @PathVariable String branch, @PathVariable String validationStamp, @PathVariable String promotionLevel) {
        int validationStampId = entityConverter.getValidationStampId(project, branch, validationStamp);
        int promotionLevelId = entityConverter.getPromotionLevelId(project, branch, promotionLevel);
        return managementService.linkValidationStampToPromotionLevel(validationStampId, promotionLevelId);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/validation_stamp/{validationStamp:[A-Za-z0-9_\\.\\-]+}/unlink", method = RequestMethod.GET)
    public
    @ResponseBody
    Ack unlinkValidationStampToPromotionLevel(@PathVariable String project, @PathVariable String branch, @PathVariable String validationStamp) {
        int validationStampId = entityConverter.getValidationStampId(project, branch, validationStamp);
        return managementService.unlinkValidationStampToPromotionLevel(validationStampId);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/promotion_level/{promotionLevel:[A-Za-z0-9_\\.\\-]+}/up", method = RequestMethod.GET)
    public
    @ResponseBody
    Ack upPromotionLevel(@PathVariable String project, @PathVariable String branch, @PathVariable String promotionLevel) {
        return managementService.upPromotionLevel(entityConverter.getPromotionLevelId(project, branch, promotionLevel));
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/promotion_level/{promotionLevel:[A-Za-z0-9_\\.\\-]+}/down", method = RequestMethod.GET)
    public
    @ResponseBody
    Ack downPromotionLevel(@PathVariable String project, @PathVariable String branch, @PathVariable String promotionLevel) {
        return managementService.downPromotionLevel(entityConverter.getPromotionLevelId(project, branch, promotionLevel));
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/promotion_level_manage", method = RequestMethod.GET)
    public
    @ResponseBody
    PromotionLevelManagementData getPromotionLevelManagementData(@PathVariable String project, @PathVariable String branch) {
        int branchId = entityConverter.getBranchId(project, branch);
        return managementService.getPromotionLevelManagementData(branchId);
    }

    // Builds

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/build", method = RequestMethod.GET)
    public
    @ResponseBody
    BranchBuilds getBuildList(Locale locale, @PathVariable String project, @PathVariable String branch,
                              @RequestParam(required = false, defaultValue = "0") int offset,
                              @RequestParam(required = false, defaultValue = "10") int count) {
        int branchId = entityConverter.getBranchId(project, branch);
        return managementService.getBuildList(locale, branchId, offset, count);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/query", method = RequestMethod.POST)
    public
    @ResponseBody
    BranchBuilds queryBuilds(Locale locale, @PathVariable String project, @PathVariable String branch, @RequestBody BuildFilter filter) {
        int branchId = entityConverter.getBranchId(project, branch);
        return managementService.queryBuilds(locale, branchId, filter);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/build/last", method = RequestMethod.GET)
    public
    @ResponseBody
    BuildSummary getLastBuild(@PathVariable String project, @PathVariable String branch) {
        int branchId = entityConverter.getBranchId(project, branch);
        return managementService.getLastBuild(branchId);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/build/withValidationStamp/{validationStamp:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.GET)
    public
    @ResponseBody
    BuildSummary getLastBuildWithValidationStamp(Locale locale, @PathVariable String project, @PathVariable String branch, @PathVariable String validationStamp) {
        int branchId = entityConverter.getBranchId(project, branch);
        return managementService.queryLastBuildWithValidationStamp(locale, branchId, validationStamp, Collections.singleton(Status.PASSED));
    }

	@Override
	@RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/build/withPromotionLevel/{promotionLevel:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.GET)
	public
	@ResponseBody
	BuildSummary getLastBuildWithPromotionLevel(Locale locale, @PathVariable String project, @PathVariable String branch, @PathVariable String promotionLevel) {
		int branchId = entityConverter.getBranchId(project, branch);
		return managementService.queryLastBuildWithPromotionLevel(locale, branchId, promotionLevel);
	}

	@Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/build/{name:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.GET)
    public
    @ResponseBody
    BuildSummary getBuild(@PathVariable String project, @PathVariable String branch, @PathVariable String name) {
        int buildId = entityConverter.getBuildId(project, branch, name);
        return managementService.getBuild(buildId);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/build/{name:[A-Za-z0-9_\\.\\-]+}/validationStamps", method = RequestMethod.GET)
    public
    @ResponseBody
    List<BuildValidationStamp> getBuildValidationStamps(Locale locale, @PathVariable String project, @PathVariable String branch, @PathVariable String name) {
        int buildId = entityConverter.getBuildId(project, branch, name);
        return managementService.getBuildValidationStamps(locale, buildId);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/build/{name:[A-Za-z0-9_\\.\\-]+}/promotionLevels", method = RequestMethod.GET)
    public
    @ResponseBody
    List<BuildPromotionLevel> getBuildPromotionLevels(Locale locale, @PathVariable String project, @PathVariable String branch, @PathVariable String name) {
        int buildId = entityConverter.getBuildId(project, branch, name);
        return managementService.getBuildPromotionLevels(locale, buildId);
    }

    // Validation runs

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/build/{build:[A-Za-z0-9_\\.\\-]+}/validation_stamp/{validationStamp:[A-Za-z0-9_\\.\\-]+}/validation_run/{run:[0-9]+}", method = RequestMethod.GET)
    public
    @ResponseBody
    ValidationRunSummary getValidationRun(@PathVariable String project, @PathVariable String branch, @PathVariable String build, @PathVariable String validationStamp, @PathVariable int run) {
        int runId = entityConverter.getValidationRunId(project, branch, build, validationStamp, run);
        return managementService.getValidationRun(runId);
    }

    @Override
    @RequestMapping(value = "/ui/manage/validation_run/{runId:[0-9]+}/comment", method = RequestMethod.POST)
    public
    @ResponseBody
    Ack addValidationRunComment(@PathVariable int runId, @RequestBody ValidationRunCommentCreationForm form) {
        return managementService.addValidationRunComment(runId, form);
    }
}
