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

import java.util.List;
import java.util.regex.Pattern;

@Controller
public class ManageUIController extends AbstractEntityUIController implements ManageUI {
	
	private final Pattern numeric = Pattern.compile("[0-9]+");
	
	private final ManagementService managementService;

	@Autowired
	public ManageUIController(ErrorHandler errorHandler, Strings strings, ManagementService managementService, EntityConverter entityConverter) {
		super(errorHandler, strings, entityConverter);
		this.managementService = managementService;
	}
	
	// Project groups

	@Override
	@RequestMapping(value = "/ui/manage/projectgroup/all", method = RequestMethod.GET)
	public @ResponseBody List<ProjectGroupSummary> getProjectGroupList() {
		return managementService.getProjectGroupList();
	}

	@Override
	@RequestMapping(value = "/ui/manage/projectgroup", method = RequestMethod.POST)
	public @ResponseBody ProjectGroupSummary createProjectGroup(@RequestBody ProjectGroupCreationForm form) {
		return managementService.createProjectGroup(form);
	}
	
	// Projects

	@Override
	@RequestMapping(value = "/ui/manage/project/all", method = RequestMethod.GET)
	public @ResponseBody List<ProjectSummary> getProjectList() {
		return managementService.getProjectList();
	}

	@Override
	@RequestMapping(value = "/ui/manage/project", method = RequestMethod.POST)
	public @ResponseBody ProjectSummary createProject(@RequestBody ProjectCreationForm form) {
		return managementService.createProject(form);
	}
	
	@Override
	@RequestMapping(value = "/ui/manage/project/{name:[A-Z0-9_\\.]+}", method = RequestMethod.GET)
	public @ResponseBody ProjectSummary getProject(@PathVariable String name) {
		return managementService.getProject(entityConverter.getProjectId(name));
	}

	protected boolean isNumeric(String value) {
		return numeric.matcher(value).matches();
	}
	
	@Override
	@RequestMapping(value = "/ui/manage/project/{name:[A-Z0-9_\\.]+}", method = RequestMethod.DELETE)
	public @ResponseBody Ack deleteProject(@PathVariable String name) {
		return managementService.deleteProject(entityConverter.getProjectId(name));
	}
	
	// Branches

	@Override
	@RequestMapping(value = "/ui/manage/branch/{project:[A-Z0-9_\\.]+}/all", method = RequestMethod.GET)
	public @ResponseBody List<BranchSummary> getBranchList(@PathVariable String project) {
		int projectId = entityConverter.getProjectId(project);
		return managementService.getBranchList(projectId);
	}
	
	@Override
	@RequestMapping(value = "/ui/manage/branch/{project:[A-Z0-9_\\.]+}/{name:[A-Z0-9_\\.]+}", method = RequestMethod.GET)
	public @ResponseBody BranchSummary getBranch(@PathVariable String project, @PathVariable String name) {
		int branchId = entityConverter.getBranchId(project, name);
		return managementService.getBranch(branchId);
	}
	
	@Override
	@RequestMapping(value = "/ui/manage/branch/{project:[A-Z0-9_\\.]+}", method = RequestMethod.POST)
	public @ResponseBody BranchSummary createBranch(@PathVariable String project, @RequestBody BranchCreationForm form) {
		int projectId = entityConverter.getProjectId(project);
		return managementService.createBranch (projectId, form);
	}

    @Override
    @RequestMapping(value = "/ui/manage/branch/{project:[A-Z0-9_\\.]+}/{name:[A-Z0-9_\\.]+}", method = RequestMethod.DELETE)
    public @ResponseBody Ack deleteBranch(@PathVariable String project, @PathVariable String name) {
        int branchId = entityConverter.getBranchId(project, name);
        return managementService.deleteBranch(branchId);
    }

    // Validation stamps

	@Override
	@RequestMapping(value = "/ui/manage/validation_stamp/{project:[A-Z0-9_\\.]+}/{branch:[A-Z0-9_\\.]+}/all", method = RequestMethod.GET)
	public @ResponseBody List<ValidationStampSummary> getValidationStampList(@PathVariable String project, @PathVariable String branch) {
		int branchId = entityConverter.getBranchId(project, branch);
		return managementService.getValidationStampList(branchId);
	}
	
	@Override
	@RequestMapping(value = "/ui/manage/validation_stamp/{project:[A-Z0-9_\\.]+}/{branch:[A-Z0-9_\\.]+}/{name:[A-Z0-9_\\.]+}", method = RequestMethod.GET)
	public @ResponseBody ValidationStampSummary getValidationStamp(@PathVariable String project, @PathVariable String branch, @PathVariable String name) {
		int validationStampId = entityConverter.getValidationStampId(project, branch, name);
		return managementService.getValidationStamp(validationStampId);
	}
	
	@Override
	@RequestMapping(value = "/ui/manage/validation_stamp/{project:[A-Z0-9_\\.]+}/{branch:[A-Z0-9_\\.]+}", method = RequestMethod.POST)
	public @ResponseBody ValidationStampSummary createValidationStamp(@PathVariable String project, @PathVariable String branch, @RequestBody ValidationStampCreationForm form) {
		int branchId = entityConverter.getBranchId(project, branch);
		return managementService.createValidationStamp (branchId, form);
	}
	
	@Override
	@RequestMapping(value = "/ui/manage/validation_stamp/{project:[A-Z0-9_\\.]+}/{branch:[A-Z0-9_\\.]+}/{name:[A-Z0-9_\\.]+}/image", method = RequestMethod.POST)
	public @ResponseBody Ack setImageValidationStamp(@PathVariable String project, @PathVariable String branch, @PathVariable String name, @RequestParam MultipartFile image) {
		int validationStampId = entityConverter.getValidationStampId(project, branch, name);
		return managementService.imageValidationStamp(validationStampId, image);
	}
	
	@Override
	@RequestMapping(value = "/ui/manage/validation_stamp/{project:[A-Z0-9_\\.]+}/{branch:[A-Z0-9_\\.]+}/{name:[A-Z0-9_\\.]+}/image", method = RequestMethod.GET)
	public @ResponseBody byte[] imageValidationStamp(@PathVariable String project, @PathVariable String branch, @PathVariable String name) {
		int validationStampId = entityConverter.getValidationStampId(project, branch, name);
		return managementService.imageValidationStamp(validationStampId);
	}
	
	// Builds

	@Override
	@RequestMapping(value = "/ui/manage/build/{project:[A-Z0-9_\\.]+}/{branch:[A-Z0-9_\\.]+}", method = RequestMethod.GET)
	public @ResponseBody
    BranchBuilds getBuildList(@PathVariable String project, @PathVariable String branch,
                                           @RequestParam(required = false, defaultValue = "0") int offset,
                                           @RequestParam(required = false, defaultValue = "10") int count) {
		int branchId = entityConverter.getBranchId(project, branch);
		return managementService.getBuildList(branchId, offset, count);
	}
	
	@Override
	@RequestMapping(value = "/ui/manage/build/{project:[A-Z0-9_\\.]+}/{branch:[A-Z0-9_\\.]+}/{name:[A-Z0-9_\\.]+}", method = RequestMethod.GET)
	public @ResponseBody BuildSummary getBuild(@PathVariable String project, @PathVariable String branch, @PathVariable String name) {
		int buildId = entityConverter.getBuildId(project, branch, name);
		return managementService.getBuild(buildId);
	}

    @Override
    @RequestMapping(value = "/ui/manage/build/{project:[A-Z0-9_\\.]+}/{branch:[A-Z0-9_\\.]+}/{name:[A-Z0-9_\\.]+}/validationStamps", method = RequestMethod.GET)
    public @ResponseBody List<BuildValidationStamp> getBuildValidationStamps(@PathVariable String project, @PathVariable String branch, @PathVariable String name) {
        int buildId = entityConverter.getBuildId(project, branch, name);
        return managementService.getBuildValidationStamps(buildId);
    }

    // Validation runs

    @Override
    @RequestMapping(value = "/ui/manage/validation_run/{runId:[0-9]+}", method = RequestMethod.GET)
    public @ResponseBody ValidationRunSummary getValidationRun(@PathVariable int runId) {
        return managementService.getValidationRun(runId);
    }

    @Override
    @RequestMapping(value = "/ui/manage/validation_run/{runId:[0-9]+}/comment", method = RequestMethod.POST)
    public @ResponseBody Ack addValidationRunComment(@PathVariable int runId, @RequestBody ValidationRunCommentCreationForm form) {
        return managementService.addValidationRunComment(runId, form);
    }
}
