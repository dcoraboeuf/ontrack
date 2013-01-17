package net.ontrack.web.ui;

import net.ontrack.core.model.ProjectGroupCreationForm;
import net.ontrack.core.model.ProjectGroupSummary;
import net.ontrack.core.ui.ManageUI;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ManageUIController implements ManageUI {
	
	@Override
	@RequestMapping(value = "/ui/manage/projectgroup", method = RequestMethod.POST)
	public @ResponseBody ProjectGroupSummary createProjectGroup (@RequestBody ProjectGroupCreationForm form) {
		// FIXME ManageUIController.createProjectGroup
		return null;
	}

}
