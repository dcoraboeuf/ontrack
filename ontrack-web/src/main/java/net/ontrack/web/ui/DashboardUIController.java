package net.ontrack.web.ui;

import net.ontrack.core.model.DashboardPage;
import net.ontrack.service.DashboardService;
import net.ontrack.web.support.AbstractUIController;
import net.ontrack.web.support.EntityConverter;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@Controller
public class DashboardUIController extends AbstractUIController {

    private final EntityConverter entityConverter;
    private final DashboardService dashboardService;

    @Autowired
    public DashboardUIController(ErrorHandler errorHandler, Strings strings, EntityConverter entityConverter, DashboardService dashboardService) {
        super(errorHandler, strings);
        this.entityConverter = entityConverter;
        this.dashboardService = dashboardService;
    }


    /**
     * General dashboard.
     */
    @RequestMapping(value = "/ui/dashboard/page/{page:[0-9]+}", method = RequestMethod.GET)
    public
    @ResponseBody
    DashboardPage generalDashboard(Locale locale, @PathVariable int page) {
        return dashboardService.getGeneralDashboardPage(locale, page);
    }

    /**
     * Project dashboard
     */
    @RequestMapping(value = "/ui/dashboard/project/{project:[A-Za-z0-9_\\.\\-]+}/page/{page:[0-9]+}", method = RequestMethod.GET)
    public
    @ResponseBody
    DashboardPage projectDashboard(Locale locale, @PathVariable int page, @PathVariable String project) {
        return dashboardService.getProjectDashboardPage(locale, entityConverter.getProjectId(project), page);
    }

    /**
     * Branch dashboard
     */
    @RequestMapping(value = "/ui/dashboard/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/page/{page:[0-9]+}", method = RequestMethod.GET)
    public
    @ResponseBody
    DashboardPage getBranchDashboardPage(Locale locale, @PathVariable int page, @PathVariable String project, @PathVariable String branch) {
        return dashboardService.getBranchDashboardPage(locale, entityConverter.getBranchId(project, branch), page);
    }
}
