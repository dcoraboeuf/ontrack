package net.ontrack.web.ui;

import net.ontrack.core.model.*;
import net.ontrack.core.ui.ControlUI;
import net.ontrack.service.ControlService;
import net.ontrack.web.support.EntityConverter;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/ui/control")
public class ControlUIController extends AbstractEntityUIController implements ControlUI {

    private final ControlService controlService;

    @Autowired
    public ControlUIController(ErrorHandler errorHandler, Strings strings, EntityConverter entityConverter, ControlService controlService) {
        super(errorHandler, strings, entityConverter);
        this.controlService = controlService;
    }

    @Override
    @RequestMapping(value = "/project/{project:[A-Za-z0-9_\\.]+}/branch/{branch:[A-Za-z0-9_\\.]+}/build", method = RequestMethod.POST)
    public
    @ResponseBody
    BuildSummary createBuild(@PathVariable String project, @PathVariable String branch, @RequestBody BuildCreationForm build) {
        int branchId = entityConverter.getBranchId(project, branch);
        return controlService.createBuild(branchId, build);
    }

    @Override
    @RequestMapping(value = "/project/{project:[A-Za-z0-9_\\.]+}/branch/{branch:[A-Za-z0-9_\\.]+}/build/{build:[A-Za-z0-9_\\.]+}/validation_stamp/{validationStamp:[A-Za-z0-9_\\.]+}", method = RequestMethod.POST)
    public
    @ResponseBody
    ValidationRunSummary createValidationRun(@PathVariable String project, @PathVariable String branch, @PathVariable String build, @PathVariable String validationStamp, @RequestBody ValidationRunCreationForm validationRun) {
        int buildId = entityConverter.getBuildId(project, branch, build);
        int validationStampId = entityConverter.getValidationStampId(project, branch, validationStamp);
        return controlService.createValidationRun(buildId, validationStampId, validationRun);
    }

    @Override
    @RequestMapping(value = "/project/{project:[A-Za-z0-9_\\.]+}/branch/{branch:[A-Za-z0-9_\\.]+}/build/{build:[A-Za-z0-9_\\.]+}/promotion_level/{promotionLevel:[A-Za-z0-9_\\.]+}", method = RequestMethod.POST)
    public
    @ResponseBody
    PromotedRunSummary createPromotedRun(@PathVariable String project, @PathVariable String branch, @PathVariable String build, @PathVariable String promotionLevel, @RequestBody PromotedRunCreationForm form) {
        int buildId = entityConverter.getBuildId(project, branch, build);
        int promotionLevelId = entityConverter.getPromotionLevelId(project, branch, promotionLevel);
        return controlService.createPromotedRun(buildId, promotionLevelId, form);
    }

}
