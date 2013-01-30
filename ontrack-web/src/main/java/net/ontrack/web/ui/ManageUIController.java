package net.ontrack.web.ui;

import java.util.List;
import java.util.regex.Pattern;

import net.ontrack.core.model.Ack;
import net.ontrack.core.model.Entity;
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
	
	private final Pattern numeric = Pattern.compile("[0-9]+");

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
	@RequestMapping(value = "/ui/manage/project/{idOrName:[A-Z0-9_\\.]+}", method = RequestMethod.GET)
	public @ResponseBody ProjectSummary getProject(@PathVariable String idOrName) {
		return managementService.getProject(getId (Entity.PROJECT, idOrName));
	}

	protected boolean isNumeric(String value) {
		return numeric.matcher(value).matches();
	}
	
	@Override
	@RequestMapping(value = "/ui/manage/project/{idOrName:[A-Z0-9_\\.]+}", method = RequestMethod.DELETE)
	public @ResponseBody Ack deleteProject(@PathVariable String idOrName) {
		return managementService.deleteProject(getId (Entity.PROJECT, idOrName));
	}

	protected int getId(Entity entity, String idOrName) {
		if (isNumeric(idOrName)) {
			return Integer.parseInt(idOrName, 10);
		} else {
			return managementService.getEntityId(entity, idOrName);
		}
	}

}
