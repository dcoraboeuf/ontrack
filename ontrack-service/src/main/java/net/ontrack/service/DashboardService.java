package net.ontrack.service;

import net.ontrack.core.model.*;

import java.util.List;
import java.util.Locale;

public interface DashboardService {

    // Dashboard root

    Dashboard getGeneralDashboard(Locale locale);

    Dashboard getProjectDashboard(Locale locale, int projectId);

    Dashboard getBranchDashboard(Locale locale, int branchId);

    // Branch page

    DashboardPage getBranchPage(Locale locale, int branchId);

    // Administration

    DashboardBranchAdmin getBranchDashboardAdminData(int branchId);

    Ack associateBranchValidationStamp(int branchId, int validationStampId);

    Ack dissociateBranchValidationStamp(int branchId, int validationStampId);

    // Custom dashboards

    List<DashboardConfig> getDashboardConfigs();

    DashboardConfig createDashboardConfig(DashboardConfigForm form);

    DashboardConfig getDashboardConfig(int id);

    DashboardConfig updateDashboardConfig(int id, DashboardConfigForm form);

    Ack deleteDashboardConfig(int id);
}
