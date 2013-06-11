package net.ontrack.service;

import net.ontrack.core.model.Dashboard;
import net.ontrack.core.model.DashboardPage;
import net.ontrack.core.model.DashboardStatusSection;

import java.util.Locale;

public interface DashboardService {

    // Dashboard root

    Dashboard getGeneralDashboard(Locale locale);

    Dashboard getProjectDashboard(Locale locale, int projectId);

    Dashboard getBranchDashboard(Locale locale, int branchId);

    // Branch status

    DashboardStatusSection getBranchStatus(Locale locale, int branchId);

    // Branch page

    DashboardPage getBranchPage(Locale locale, int branchId);
}
