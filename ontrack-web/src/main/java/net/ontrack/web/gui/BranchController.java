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
@RequestMapping("/gui/branch")
public class BranchController extends AbstractGUIController {

	private final ManageUI manageUI;

	@Autowired
	public BranchController(ErrorHandler errorHandler, ManageUI manageUI) {
		super(errorHandler);
		this.manageUI = manageUI;
	}

	@RequestMapping(value = "/{project:[A-Z0-9_\\.]+}/{name:[A-Z0-9_\\.]+}", method = RequestMethod.GET)
	public String getBranch(Model model, @PathVariable String project, @PathVariable String name) {
		// Loads the details
		model.addAttribute("branch", manageUI.getBranch(project, name));
		// OK
		return "branch";
	}

	@RequestMapping(value = "/{id:\\d+}", method = RequestMethod.GET)
	public String getBranch(Model model, @PathVariable int id) {
		// Loads the details
		model.addAttribute("branch", manageUI.getBranch(id));
		// OK
		return "branch";
	}

}
