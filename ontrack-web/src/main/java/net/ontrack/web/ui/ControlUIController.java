package net.ontrack.web.ui;

import net.ontrack.core.model.BuildCreationForm;
import net.ontrack.core.model.BuildSummary;
import net.ontrack.core.model.ValidationRunCreationForm;
import net.ontrack.core.model.ValidationRunSummary;
import net.ontrack.core.ui.ControlUI;
import net.ontrack.service.ControlService;
import net.ontrack.service.ManagementService;
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
@RequestMapping("/ui/control")
public class ControlUIController extends AbstractEntityUIController implements ControlUI {

	private final ControlService controlService;

	@Autowired
	public ControlUIController(ErrorHandler errorHandler, Strings strings, ManagementService managementService, ControlService controlService) {
		super(errorHandler, strings, managementService);
		this.controlService = controlService;
	}

	@Override
	@RequestMapping(value = "/build/{project:[A-Z0-9_\\.]+}/{branch:[A-Z0-9_\\.]+}", method = RequestMethod.POST)
	public @ResponseBody BuildSummary createBuild(@PathVariable String project, @PathVariable String branch, @RequestBody BuildCreationForm build) {
		int branchId = getBranchId(project, branch);
		return controlService.createBuild(branchId, build);
	}

	@Override
	@RequestMapping(value = "/validation/{project:[A-Z0-9_\\.]+}/{branch:[A-Z0-9_\\.]+}/{validationStamp:[A-Z0-9_\\.]+}/{build:[A-Z0-9_\\.]+}", method = RequestMethod.POST)
	public @ResponseBody ValidationRunSummary createValidationRun(@PathVariable String project, @PathVariable String branch, @PathVariable String build, @PathVariable String validationStamp, @RequestBody ValidationRunCreationForm validationRun) {
		// FIXME Implement ControlUI.createValidationRun
		throw new RuntimeException("NYI");
	}

}
