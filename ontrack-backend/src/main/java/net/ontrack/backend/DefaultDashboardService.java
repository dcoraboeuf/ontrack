package net.ontrack.backend;

import net.ontrack.core.model.BranchSummary;
import net.ontrack.core.model.Dashboard;
import net.ontrack.core.model.ProjectSummary;
import net.ontrack.service.DashboardService;
import net.ontrack.service.ManagementService;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class DefaultDashboardService implements DashboardService {

    private final ManagementService managementService;
    private final Strings strings;

    @Autowired
    public DefaultDashboardService(ManagementService managementService, Strings strings) {
        this.managementService = managementService;
        this.strings = strings;
    }

    @Override
    @Transactional(readOnly = true)
    public Dashboard getGeneralDashboard(Locale locale) {
        return new Dashboard(
                strings.get(locale, "app.title")
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Dashboard getProjectDashboard(Locale locale, int projectId) {
        ProjectSummary project = managementService.getProject(projectId);
        return new Dashboard(
                project.getName()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Dashboard getBranchDashboard(Locale locale, int branchId) {
        BranchSummary branch = managementService.getBranch(branchId);
        return new Dashboard(
                branch.getProject().getName() + "/" + branch.getName()
        );
    }
}
