package net.ontrack.service;

import net.ontrack.core.model.Dashboard;
import net.ontrack.core.model.DashboardPage;

import java.util.Locale;

public interface DashboardService {

    Dashboard getGeneralDashboard(Locale locale);

    Dashboard getProjectDashboard(Locale locale, int projectId);

    Dashboard getBranchDashboard(Locale locale, int branchId);

    @Deprecated
    DashboardPage getGeneralDashboardPage(Locale locale, int page);

    @Deprecated
    DashboardPage getProjectDashboardPage(Locale locale, int projectId, int page);

    @Deprecated
    DashboardPage getBranchDashboardPage(Locale locale, int branchId, int page);
}
