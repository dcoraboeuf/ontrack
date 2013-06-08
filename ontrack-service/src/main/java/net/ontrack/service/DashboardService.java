package net.ontrack.service;

import net.ontrack.core.model.Dashboard;

import java.util.Locale;

public interface DashboardService {

    Dashboard getGeneralDashboard(Locale locale);

    Dashboard getProjectDashboard(Locale locale, int projectId);

    Dashboard getBranchDashboard(Locale locale, int branchId);

}
