package net.ontrack.web.ui;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import net.ontrack.core.model.Ack;
import net.ontrack.core.model.BranchCreationForm;
import net.ontrack.core.model.BranchSummary;
import net.ontrack.core.model.BuildSummary;
import net.ontrack.core.model.Entity;
import net.ontrack.core.model.ProjectCreationForm;
import net.ontrack.core.model.ProjectGroupCreationForm;
import net.ontrack.core.model.ProjectGroupSummary;
import net.ontrack.core.model.ProjectSummary;
import net.ontrack.core.model.ValidationStampCreationForm;
import net.ontrack.core.model.ValidationStampSummary;
import net.ontrack.core.ui.ManageUI;
import net.ontrack.service.ManagementService;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Strings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ManageUIController extends AbstractEntityUIController implements ManageUI {
	
	private final Pattern numeric = Pattern.compile("[0-9]+");

	@Autowired
	public ManageUIController(ErrorHandler errorHandler, Strings strings, ManagementService managementService) {
		super(errorHandler, strings, managementService);
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
		return managementService.getProject(getId (Entity.PROJECT, name, Collections.<Entity, Integer>emptyMap()));
	}

	protected boolean isNumeric(String value) {
		return numeric.matcher(value).matches();
	}
	
	@Override
	@RequestMapping(value = "/ui/manage/project/{name:[A-Z0-9_\\.]+}", method = RequestMethod.DELETE)
	public @ResponseBody Ack deleteProject(@PathVariable String name) {
		return managementService.deleteProject(getId (Entity.PROJECT, name, Collections.<Entity, Integer>emptyMap()));
	}
	
	// Branches

	@Override
	@RequestMapping(value = "/ui/manage/branch/{project:[A-Z0-9_\\.]+}/all", method = RequestMethod.GET)
	public @ResponseBody List<BranchSummary> getBranchList(@PathVariable String project) {
		int projectId = getProjectId(project);
		return managementService.getBranchList(projectId);
	}
	
	@Override
	@RequestMapping(value = "/ui/manage/branch/{project:[A-Z0-9_\\.]+}/{idOrName:[A-Z0-9_\\.]+}", method = RequestMethod.GET)
	public @ResponseBody BranchSummary getBranch(@PathVariable String project, @PathVariable String name) {
		int branchId = getBranchId(project, name);
		return managementService.getBranch(branchId);
	}
	
	@Override
	@RequestMapping(value = "/ui/manage/branch/{project:[A-Z0-9_\\.]+}", method = RequestMethod.POST)
	public @ResponseBody BranchSummary createBranch(@PathVariable String project, @RequestBody BranchCreationForm form) {
		int projectId = getProjectId(project);
		return managementService.createBranch (projectId, form);
	}
	
	// Validation stamps

	@Override
	@RequestMapping(value = "/ui/manage/validation_stamp/{project:[A-Z0-9_\\.]+}/{branch:[A-Z0-9_\\.]+}/all", method = RequestMethod.GET)
	public @ResponseBody List<ValidationStampSummary> getValidationStampList(@PathVariable String project, @PathVariable String branch) {
		int branchId = getBranchId(project, branch);
		return managementService.getValidationStampList(branchId);
	}
	
	@Override
	@RequestMapping(value = "/ui/manage/validation_stamp/{project:[A-Z0-9_\\.]+}/{branch:[A-Z0-9_\\.]+}/{name:[A-Z0-9_\\.]+}", method = RequestMethod.GET)
	public @ResponseBody ValidationStampSummary getValidationStamp(@PathVariable String project, @PathVariable String branch, @PathVariable String name) {
		int validationStampId = getValidationStampId(project, branch, name);
		return managementService.getValidationStamp(validationStampId);
	}
	
	@Override
	@RequestMapping(value = "/ui/manage/validation_stamp/{project:[A-Z0-9_\\.]+}/{branch:[A-Z0-9_\\.]+}", method = RequestMethod.POST)
	public @ResponseBody ValidationStampSummary createValidationStamp(@PathVariable String project, @PathVariable String branch, @RequestBody ValidationStampCreationForm form) {
		int branchId = getBranchId(project, branch);
		return managementService.createValidationStamp (branchId, form);
	}
	
	@Override
	@RequestMapping(value = "/ui/manage/validation_stamp/{project:[A-Z0-9_\\.]+}/{branch:[A-Z0-9_\\.]+}/{name:[A-Z0-9_\\.]+}/image", method = RequestMethod.POST)
	public @ResponseBody Ack setImageValidationStamp(@PathVariable String project, @PathVariable String branch, @PathVariable String name, @RequestParam MultipartFile image) {
		int validationStampId = getValidationStampId(project, branch, name);
		return managementService.imageValidationStamp(validationStampId, image);
	}
	
	@Override
	@RequestMapping(value = "/ui/manage/validation_stamp/{project:[A-Z0-9_\\.]+}/{branch:[A-Z0-9_\\.]+}/{name:[A-Z0-9_\\.]+}/image", method = RequestMethod.GET)
	public HttpEntity<byte[]> getImageValidationStamp(@PathVariable String project, @PathVariable String branch, @PathVariable String name) {
		int validationStampId = getValidationStampId(project, branch, name);
		byte[] content = managementService.imageValidationStamp(validationStampId);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_PNG);
		headers.setContentLength(content.length);
		return new ResponseEntity<>(headers, HttpStatus.OK);
	}
	
	// Builds

	@Override
	@RequestMapping(value = "/ui/manage/build/{project:[A-Z0-9_\\.]+}/{branch:[A-Z0-9_\\.]+}/all", method = RequestMethod.GET)
	public @ResponseBody List<BuildSummary> getBuildList(@PathVariable String project, @PathVariable String branch) {
		int branchId = getBranchId(project, branch);
		return managementService.getBuildList(branchId);
	}
	
	@Override
	@RequestMapping(value = "/ui/manage/build/{project:[A-Z0-9_\\.]+}/{branch:[A-Z0-9_\\.]+}/{name:[A-Z0-9_\\.]+}", method = RequestMethod.GET)
	public @ResponseBody BuildSummary getBuild(@PathVariable String project, @PathVariable String branch, @PathVariable String name) {
		int buildId = getBuildId(project, branch, name);
		return managementService.getBuild(buildId);
	}

}
