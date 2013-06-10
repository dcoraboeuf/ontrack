package net.ontrack.backend;

import net.ontrack.core.model.BranchSummary;
import net.ontrack.core.model.Dashboard;
import net.ontrack.core.model.DashboardPage;
import net.ontrack.core.model.ProjectSummary;
import net.ontrack.service.DashboardService;
import net.ontrack.service.ManagementService;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
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
                strings.get(locale, "dashboard.general")
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Dashboard getProjectDashboard(Locale locale, int projectId) {
        ProjectSummary project = managementService.getProject(projectId);
        return new Dashboard(
                strings.get(locale, "dashboard.project", project.getName())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Dashboard getBranchDashboard(Locale locale, int branchId) {
        BranchSummary branch = managementService.getBranch(branchId);
        return new Dashboard(
                strings.get(locale, "dashboard.branch", branch.getProject().getName(), branch.getName())
        );
    }

    /**
     * One page per project branch
     */
    @Override
    @Transactional(readOnly = true)
    public DashboardPage getGeneralDashboardPage(Locale locale, int page) {
        // Gets the list of branches
        List<BranchSummary> branchList = new ArrayList<>();
        for (ProjectSummary projectSummary : managementService.getProjectList()) {
            for (BranchSummary branchSummary : managementService.getBranchList(projectSummary.getId())) {
                branchList.add(branchSummary);
            }
        }
        // Index
        int index = page % branchList.size();
        // Returns the page for the branch
        return getBranchDashboardPage(locale, branchList.get(index).getId(), 0);
    }

    /**
     * One page per branch
     */
    @Override
    @Transactional(readOnly = true)
    public DashboardPage getProjectDashboardPage(Locale locale, int projectId, int page) {
        // Gets the list of branches for this project
        List<BranchSummary> branchList = managementService.getBranchList(projectId);
        // Index
        int index = page % branchList.size();
        // Returns the page for the branch
        return getBranchDashboardPage(locale, branchList.get(index).getId(), 0);
    }

    /**
     * FIXME The branch dashboard is a single page.
     */
    @Override
    @Transactional(readOnly = true)
    public DashboardPage getBranchDashboardPage(Locale locale, int branchId, int page) {
        BranchSummary branch = managementService.getBranch(branchId);
        return new DashboardPage(
                branch.getProject().getName() + "/" + branch.getName()
        );
    }
}
