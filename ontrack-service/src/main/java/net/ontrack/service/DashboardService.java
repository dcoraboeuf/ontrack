package net.ontrack.service;

import net.ontrack.core.model.Dashboard;
import net.ontrack.core.model.DashboardPage;

import java.util.Locale;

public interface DashboardService {

    Dashboard getGeneralDashboard(Locale locale);

    Dashboard getProjectDashboard(Locale locale, int projectId);

    Dashboard getBranchDashboard(Locale locale, int branchId);

    DashboardPage getGeneralDashboardPage(Locale locale, int page);

    DashboardPage getProjectDashboardPage(Locale locale, int projectId, int page);

    DashboardPage getBranchDashboardPage(Locale locale, int branchId, int page);
}
