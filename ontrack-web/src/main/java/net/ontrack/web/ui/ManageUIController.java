package net.ontrack.web.ui;

import java.util.List;

import net.ontrack.core.model.ProjectCreationForm;
import net.ontrack.core.model.ProjectGroupCreationForm;
import net.ontrack.core.model.ProjectGroupSummary;
import net.ontrack.core.model.ProjectSummary;
import net.ontrack.core.ui.ManageUI;
import net.ontrack.service.ManagementService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ManageUIController implements ManageUI {

	private final ManagementService managementService;

	@Autowired
	public ManageUIController(ManagementService managementService) {
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
	@RequestMapping(value = "/ui/manage/project/{0}", method = RequestMethod.GET)
	public ProjectSummary getProject(@PathVariable int id) {
		return managementService.getProject(id);
	}

}
