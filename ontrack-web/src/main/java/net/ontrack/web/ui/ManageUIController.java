package net.ontrack.web.ui;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import net.ontrack.core.model.Ack;
import net.ontrack.core.model.BranchCreationForm;
import net.ontrack.core.model.BranchSummary;
import net.ontrack.core.model.Entity;
import net.ontrack.core.model.ProjectCreationForm;
import net.ontrack.core.model.ProjectGroupCreationForm;
import net.ontrack.core.model.ProjectGroupSummary;
import net.ontrack.core.model.ProjectSummary;
import net.ontrack.core.ui.ManageUI;
import net.ontrack.service.ManagementService;
import net.ontrack.web.support.AbstractUIController;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Strings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ManageUIController extends AbstractUIController implements ManageUI {
	
	private final Pattern numeric = Pattern.compile("[0-9]+");

	private final ManagementService managementService;

	@Autowired
	public ManageUIController(ErrorHandler errorHandler, Strings strings, ManagementService managementService) {
		super(errorHandler, strings);
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
		int projectId = getId(Entity.PROJECT, project, Collections.<Entity, Integer>emptyMap());
		return managementService.getBranchList(projectId);
	}
	
	@Override
	@RequestMapping(value = "/ui/manage/branch/{project:[A-Z0-9_\\.]+}/{idOrName:[A-Z0-9_\\.]+}", method = RequestMethod.GET)
	public @ResponseBody BranchSummary getBranch(@PathVariable String project, @PathVariable String name) {
		int projectId = getId(Entity.PROJECT, project, Collections.<Entity, Integer>emptyMap());
		int branchId = getId(Entity.BRANCH, name, Collections.singletonMap(Entity.PROJECT, projectId));
		return managementService.getBranch(branchId);
	}
	
	@Override
	@RequestMapping(value = "/ui/manage/branch/{project:[A-Z0-9_\\.]+}", method = RequestMethod.POST)
	public @ResponseBody BranchSummary createBranch(@PathVariable String project, @RequestBody BranchCreationForm form) {
		int projectId = getId(Entity.PROJECT, project, Collections.<Entity, Integer>emptyMap());
		return managementService.createBranch (projectId, form);
	}
	
	// Common

	protected int getId(Entity entity, String name, Map<Entity, Integer> parentIds) {
		return managementService.getEntityId(entity, name, parentIds);
	}

}
