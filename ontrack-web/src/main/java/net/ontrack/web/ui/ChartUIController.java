package net.ontrack.web.ui;

import net.ontrack.core.model.ChartTable;
import net.ontrack.service.ManagementService;
import net.ontrack.web.support.AbstractUIController;
import net.ontrack.web.support.EntityConverter;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class ChartUIController extends AbstractUIController {

    private final ManagementService managementService;
    private final EntityConverter entityConverter;

    @Autowired
    public ChartUIController(ErrorHandler errorHandler, Strings strings, ManagementService managementService, EntityConverter entityConverter) {
        super(errorHandler, strings);
        this.managementService = managementService;
        this.entityConverter = entityConverter;
    }

    @RequestMapping(value = "/ui/chart/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/chart/validation_stamp_statuses", method = RequestMethod.GET)
    public
    @ResponseBody
    ChartTable getChartBranchValidationStampStatuses(@PathVariable String project, @PathVariable String branch) {
        return managementService.getChartBranchValidationStampStatuses(
                entityConverter.getBranchId(project, branch)
        );
    }

    @RequestMapping(value = "/ui/chart/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/chart/validation_stamp_retries", method = RequestMethod.GET)
    public
    @ResponseBody
    Map<String, Integer> getChartBranchValidationStampRetries(@PathVariable String project, @PathVariable String branch) {
        return managementService.getChartBranchValidationStampRetries(
                entityConverter.getBranchId(project, branch)
        );
    }
}
