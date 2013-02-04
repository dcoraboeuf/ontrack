package net.ontrack.web.gui;

import net.ontrack.core.ui.ManageUI;
import net.ontrack.web.support.AbstractGUIController;
import net.ontrack.web.support.ErrorHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class GUIController extends AbstractGUIController {

	private final ManageUI manageUI;

	@Autowired
	public GUIController(ErrorHandler errorHandler, ManageUI manageUI) {
		super(errorHandler);
		this.manageUI = manageUI;
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home() {
		// OK
		return "home";
	}

	@RequestMapping(value = "/gui/project/{name:[A-Z0-9_\\.]+}", method = RequestMethod.GET)
	public String getProject(Model model, @PathVariable String name) {
		// Loads the project details
		model.addAttribute("project", manageUI.getProject(name));
		// OK
		return "project";
	}

	@RequestMapping(value = "/gui/branch/{project:[A-Z0-9_\\.]+}/{name:[A-Z0-9_\\.]+}", method = RequestMethod.GET)
	public String getBranch(Model model, @PathVariable String project, @PathVariable String name) {
		// Loads the details
		model.addAttribute("branch", manageUI.getBranch(project, name));
		// OK
		return "branch";
	}

	@RequestMapping(value = "/gui/build/{project:[A-Z0-9_\\.]+}/{branch:[A-Z0-9_\\.]+}/{name:[A-Z0-9_\\.]+}", method = RequestMethod.GET)
	public String getBranch(Model model, @PathVariable String project, @PathVariable String branch, @PathVariable String name) {
		// Loads the details
		model.addAttribute("build", manageUI.getBuild(project, branch, name));
		// OK
		return "build";
	}

}
