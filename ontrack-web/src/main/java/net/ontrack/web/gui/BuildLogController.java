package net.ontrack.web.gui;

import net.ontrack.core.model.Status;
import net.ontrack.core.ui.ManageUI;
import net.ontrack.web.gui.model.BuildLogForm;
import net.ontrack.web.support.AbstractGUIController;
import net.ontrack.web.support.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Arrays;

@Controller
public class BuildLogController extends AbstractGUIController {

    private final ManageUI manageUI;

    @Autowired
    public BuildLogController(ErrorHandler errorHandler, ManageUI manageUI) {
        super(errorHandler);
        this.manageUI = manageUI;
    }

    @RequestMapping(value = "/gui/project/{project:[A-Za-z0-9_\\.]+}/branch/{branch:[A-Za-z0-9_\\.]+}/query", method = RequestMethod.GET)
    public String query(@PathVariable String project, @PathVariable String branch, BuildLogForm query, Model model) {


        // Branch summary
        model.addAttribute("branch", manageUI.getBranch(project, branch));
        // Gets the list of promotion levels
        model.addAttribute("promotionLevels", manageUI.getPromotionLevelList(project, branch));
        // Gets the list of validation stamps
        model.addAttribute("validationStamps", manageUI.getValidationStampList(project, branch));
        // Gets the list of statuses
        model.addAttribute("statusList", Arrays.asList(Status.values()));

        // Form data
        if (query.getLimit() < 0) {
            query.setLimit(20);
        }
        model.addAttribute("query", query);

        // OK
        return "query";
    }
}
