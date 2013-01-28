package net.ontrack.web.pages;

import net.ontrack.core.ui.ManageUI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/gui/project")
public class ProjectController {

	private final ManageUI manageUI;

	@Autowired
	public ProjectController(ManageUI manageUI) {
		this.manageUI = manageUI;
	}

	@RequestMapping(value = "/{id:\\d+}", method = RequestMethod.GET)
	public String getProject(Model model, @PathVariable int id) {
		// Loads the project details
		model.addAttribute("project", manageUI.getProject(id));
		// OK
		return "project";
	}

}
