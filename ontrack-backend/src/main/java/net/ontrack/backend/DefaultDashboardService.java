package net.ontrack.backend;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import net.ontrack.backend.dao.DashboardDao;
import net.ontrack.core.model.*;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.service.DashboardSectionDecorator;
import net.ontrack.service.DashboardSectionProvider;
import net.ontrack.service.DashboardService;
import net.ontrack.service.ManagementService;
import net.sf.jstring.Strings;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class DefaultDashboardService implements DashboardService {

    private final ManagementService managementService;
    private final Strings strings;
    private final DashboardDao dashboardDao;
    private List<DashboardSectionProvider> dashboardSectionProviders;
    private List<DashboardSectionDecorator> dashboardSectionDecorators;

    @Autowired
    public DefaultDashboardService(ManagementService managementService, Strings strings, DashboardDao dashboardDao) {
        this.managementService = managementService;
        this.strings = strings;
        this.dashboardDao = dashboardDao;
    }

    @Autowired(required = false)
    public void setDashboardSectionProviders(List<DashboardSectionProvider> dashboardSectionProviders) {
        this.dashboardSectionProviders = dashboardSectionProviders;
    }

    @Autowired(required = false)
    public void setDashboardSectionDecorators(List<DashboardSectionDecorator> dashboardSectionDecorators) {
        this.dashboardSectionDecorators = dashboardSectionDecorators;
    }

    @Override
    @Transactional(readOnly = true)
    public Dashboard getGeneralDashboard(Locale locale) {
        // Gets the list of branches
        List<BranchSummary> branchList = new ArrayList<>();
        for (ProjectSummary projectSummary : managementService.getProjectList()) {
            for (BranchSummary branchSummary : managementService.getBranchList(projectSummary.getId())) {
                branchList.add(branchSummary);
            }
        }
        // OK
        return new Dashboard(
                strings.get(locale, "dashboard.general"),
                branchList
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Dashboard getProjectDashboard(Locale locale, int projectId) {
        ProjectSummary project = managementService.getProject(projectId);
        // Gets the list of branches for this project
        List<BranchSummary> branchList = managementService.getBranchList(projectId);
        // OK
        return new Dashboard(
                strings.get(locale, "dashboard.project", project.getName()),
                branchList
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Dashboard getBranchDashboard(Locale locale, int branchId) {
        BranchSummary branch = managementService.getBranch(branchId);
        return new Dashboard(
                strings.get(locale, "dashboard.branch", branch.getProject().getName(), branch.getName()),
                Collections.singletonList(branch)
        );
    }

    private String getBranchTitle(BranchSummary branch) {
        return branch.getProject().getName() + "/" + branch.getName();
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardPage getBranchPage(Locale locale, final int branchId) {
        BranchSummary branch = managementService.getBranch(branchId);

        // All sections
        List<DashboardSection> sections = new ArrayList<>();

        // Dashboard section providers
        if (dashboardSectionProviders != null) {
            for (DashboardSectionProvider dashboardSectionProvider : dashboardSectionProviders) {
                if (dashboardSectionProvider.apply(Entity.BRANCH, branchId)) {
                    DashboardSection section = dashboardSectionProvider.getSection(Entity.BRANCH, branchId);
                    if (section != null) {
                        sections.add(section);
                    }
                }
            }
        }

        // One section per configured validation stamp

        // Gets all validation stamps for the branch
        List<ValidationStampSummary> stamps = managementService.getValidationStampList(branchId);
        // Filters them out according to the dashboard configuration
        Collection<ValidationStampSummary> selectedStamps = Collections2.filter(stamps, new Predicate<ValidationStampSummary>() {
            @Override
            public boolean apply(ValidationStampSummary stamp) {
                return dashboardDao.isValidationStampSelectedForBranch(stamp.getId(), branchId);
            }
        });
        // Collects the statuses
        Collection<ValidationStampStatus> stampWithStatuses = Collections2.transform(
                selectedStamps,
                // Gets the last validation run for each stamp
                new Function<ValidationStampSummary, ValidationStampStatus>() {
                    @Override
                    public ValidationStampStatus apply(ValidationStampSummary stamp) {
                        List<ValidationRunStatusStub> statusesForLastBuilds = managementService.getStatusesForLastBuilds(stamp.getId(), 1);
                        if (statusesForLastBuilds.isEmpty()) {
                            return new ValidationStampStatus(stamp, null);
                        } else {
                            return new ValidationStampStatus(stamp, statusesForLastBuilds.get(0));
                        }
                    }
                }
        );
        // Generates the sections
        Collection<DashboardSection> stampSections = Collections2.transform(
                stampWithStatuses,
                new Function<ValidationStampStatus, DashboardSection>() {
                    @Override
                    public DashboardSection apply(ValidationStampStatus stamp) {
                        return new DashboardSection(
                                "dashboard-section",
                                getValidationStampSection(stamp)
                        );
                    }
                }
        );
        // OK
        sections.addAll(stampSections);

        // OK
        return new DashboardPage(
                getBranchTitle(branch),
                sections
        );
    }

    private DashboardSectionData getValidationStampSection(ValidationStampStatus stamp) {
        // CSS classes to apply
        Collection<String> css = new ArrayList<>();
        // Status class
        if (stamp.getStatus() != null) {
            if (stamp.getStatus().getStatus() == Status.PASSED) {
                css.add("dashboard-validation-stamp-passed");
            } else {
                css.add("dashboard-validation-stamp-failed");
            }
        } else {
            css.add("dashboard-validation-stamp-notrun");
        }
        // Additional classes
        if (dashboardSectionDecorators != null) {
            for (DashboardSectionDecorator decorator : dashboardSectionDecorators) {
                Collection<String> classes = decorator.getClasses(Entity.VALIDATION_STAMP, stamp.getStamp().getId());
                if (classes != null) {
                    css.addAll(classes);
                }
            }
        }
        // OK
        return new DashboardSectionData(
                stamp.getStamp().getName(),
                StringUtils.join(css, " ")
        );
    }

    @Override
    @Transactional(readOnly = true)
    @Secured(SecurityRoles.ADMINISTRATOR)
    public DashboardBranchAdmin getBranchDashboardAdminData(final int branchId) {
        // Gets the branch
        BranchSummary branch = managementService.getBranch(branchId);
        // Validation stamps
        List<FlaggedValidationStamp> stamps = Lists.transform(
                managementService.getValidationStampList(branchId),
                new Function<ValidationStampSummary, FlaggedValidationStamp>() {
                    @Override
                    public FlaggedValidationStamp apply(ValidationStampSummary stamp) {
                        return new FlaggedValidationStamp(
                                stamp,
                                dashboardDao.isValidationStampSelectedForBranch(stamp.getId(), branchId)
                        );
                    }
                }
        );
        // OK
        return new DashboardBranchAdmin(
                branch,
                stamps
        );
    }

    @Override
    @Transactional
    @Secured(SecurityRoles.ADMINISTRATOR)
    public Ack associateBranchValidationStamp(int branchId, int validationStampId) {
        return dashboardDao.associateBranchValidationStamp(branchId, validationStampId);
    }

    @Override
    @Transactional
    @Secured(SecurityRoles.ADMINISTRATOR)
    public Ack dissociateBranchValidationStamp(int branchId, int validationStampId) {
        return dashboardDao.dissociateBranchValidationStamp(branchId, validationStampId);
    }
}
