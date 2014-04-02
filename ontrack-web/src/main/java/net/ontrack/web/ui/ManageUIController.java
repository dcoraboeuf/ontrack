package net.ontrack.web.ui;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import net.ontrack.core.model.*;
import net.ontrack.core.security.GlobalFunction;
import net.ontrack.core.security.ProjectFunction;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.core.ui.ManageUI;
import net.ontrack.core.ui.PropertyUI;
import net.ontrack.service.DashboardService;
import net.ontrack.service.ExportService;
import net.ontrack.service.ManagementService;
import net.ontrack.service.ProfileService;
import net.ontrack.web.support.EntityConverter;
import net.ontrack.web.support.ErrorHandler;
import net.ontrack.web.ui.model.ValidationRunStatusUpdateData;
import net.sf.jstring.Strings;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.WebAsyncTask;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.CookieGenerator;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;

@Controller
public class ManageUIController extends AbstractEntityUIController implements ManageUI {

    /**
     * Timeout for the back-up (5 minutes)
     */
    public static final long BACKUP_TIMEOUT = 5 * 60 * 1000L;
    private final SecurityUtils securityUtils;
    private final ManagementService managementService;
    private final DashboardService dashboardService;
    private final ProfileService profileService;
    private final ExportService exportService;
    private final PropertyUI propertyUI;
    private final ObjectMapper objectMapper;
    private final String version;

    @Autowired
    public ManageUIController(ErrorHandler errorHandler, Strings strings, ManagementService managementService, EntityConverter entityConverter, SecurityUtils securityUtils, DashboardService dashboardService, ProfileService profileService, ExportService exportService, PropertyUI propertyUI, ObjectMapper objectMapper, @Value("${app.version}") String version) {
        super(errorHandler, strings, entityConverter);
        this.managementService = managementService;
        this.securityUtils = securityUtils;
        this.dashboardService = dashboardService;
        this.profileService = profileService;
        this.exportService = exportService;
        this.propertyUI = propertyUI;
        this.objectMapper = objectMapper;
        this.version = version;
    }

    @Override
    @RequestMapping(value = "/ui/manage/version", method = RequestMethod.GET)
    public
    @ResponseBody
    String getVersion() {
        return version;
    }

    // Projects

    @Override
    @RequestMapping(value = "/ui/manage/project", method = RequestMethod.GET)
    public
    @ResponseBody
    List<ProjectSummary> getProjectList() {
        return managementService.getProjectList();
    }

    @Override
    @RequestMapping(value = "/ui/manage/project", method = RequestMethod.POST)
    public
    @ResponseBody
    ProjectSummary createProject(@RequestBody ProjectCreationForm form) {
        return managementService.createProject(form);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{name:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.GET)
    public
    @ResponseBody
    ProjectSummary getProject(@PathVariable String name) {
        return managementService.getProject(entityConverter.getProjectId(name));
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{name:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    Ack deleteProject(@PathVariable String name) {
        return managementService.deleteProject(entityConverter.getProjectId(name));
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{name:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.PUT)
    public
    @ResponseBody
    ProjectSummary updateProject(@PathVariable String name, @RequestBody ProjectUpdateForm form) {
        return managementService.updateProject(
                entityConverter.getProjectId(name),
                form
        );
    }

    // Project IO

    @Override
    public ExportData backupSave() throws Exception {
        return (ExportData) doBackupSave().getCallable().call();
    }

    @RequestMapping(value = "/ui/manage/backup", method = RequestMethod.GET)
    public
    @ResponseBody
    WebAsyncTask<ExportData> doBackupSave() {
        securityUtils.checkGrant(GlobalFunction.PROJECT_EXPORT);
        // Launches the export with same credentials
        return new WebAsyncTask<>(
                BACKUP_TIMEOUT,
                securityUtils.withCurrentCredentials(
                        new Callable<ExportData>() {
                            @Override
                            public ExportData call() throws Exception {
                                // Gets the list of projects
                                List<ProjectSummary> projectList = managementService.getProjectList();
                                // Project IDs
                                List<Integer> ids = Lists.transform(
                                        projectList,
                                        new Function<ProjectSummary, Integer>() {
                                            @Override
                                            public Integer apply(ProjectSummary o) {
                                                return o.getId();
                                            }
                                        }
                                );
                                // Launches the export
                                String uuid = exportService.exportLaunch(ids);
                                // Waits until the export is done
                                while (!exportService.exportCheck(uuid).isSuccess()) {
                                    Thread.sleep(100);
                                }
                                // Downloads the file
                                return exportService.exportDownload(uuid);
                            }
                        }
                )
        );
    }

    @Override
    public ImportResult backupRestore(MultipartFile file) throws Exception {
        return doBackupRestore(file).call();
    }

    @RequestMapping(value = "/ui/manage/backup", method = RequestMethod.POST)
    public
    @ResponseBody
    Callable<ImportResult> doBackupRestore(@RequestParam final MultipartFile file) {
        securityUtils.checkGrant(GlobalFunction.PROJECT_CREATE);
        return securityUtils.withCurrentCredentials(
                new Callable<ImportResult>() {
                    @Override
                    public ImportResult call() throws Exception {
                        // Imports the file
                        String uuid = exportService.importLaunch(file);
                        // Waits until the import is done
                        ImportResult result;
                        while (true) {
                            result = exportService.importCheck(uuid);
                            if (result.getFinished().isSuccess()) {
                                break;
                            } else {
                                Thread.sleep(100);
                            }
                        }
                        // Gets the results
                        return result;
                    }
                }
        );
    }

    @Override
    @RequestMapping(value = "/ui/manage/export/project", method = RequestMethod.POST)
    public
    @ResponseBody
    ExportResponse exportLaunch(@RequestBody ExportForm form) {
        return new ExportResponse(
                exportService.exportLaunch(
                        Collections2.transform(
                                form.getNames(),
                                new Function<String, Integer>() {
                                    @Override
                                    public Integer apply(String name) {
                                        return entityConverter.getProjectId(name);
                                    }
                                }
                        )
                )
        );
    }

    @Override
    @RequestMapping(value = "/ui/manage/export/project/{project:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.GET)
    public
    @ResponseBody
    ExportResponse exportProjectLaunch(@PathVariable String project) {
        return new ExportResponse(
                exportService.exportLaunch(Collections.singleton(entityConverter.getProjectId(project)))
        );
    }

    @Override
    @RequestMapping(value = "/ui/manage/export/{uuid}/check", method = RequestMethod.GET)
    public
    @ResponseBody
    Ack exportProjectCheck(@PathVariable String uuid) {
        return exportService.exportCheck(uuid);
    }

    @Override
    @RequestMapping(value = "/ui/manage/export/{uuid}", method = RequestMethod.GET)
    public
    @ResponseBody
    ExportData exportProjectDownload(@PathVariable String uuid) {
        return exportService.exportDownload(uuid);
    }

    @Override
    @RequestMapping(value = "/ui/manage/import", method = RequestMethod.POST)
    public
    @ResponseBody
    ImportResponse importLaunch(@RequestParam MultipartFile file) {
        return new ImportResponse(exportService.importLaunch(file));
    }

    @Override
    @RequestMapping(value = "/ui/manage/import/{uuid}/check", method = RequestMethod.GET)
    public
    @ResponseBody
    ImportResult importCheck(@PathVariable String uuid) {
        return exportService.importCheck(uuid);
    }

    @Override
    @RequestMapping(value = "/ui/manage/branch", method = RequestMethod.GET)
    @ResponseBody
    public List<BranchSummary> getBranchAll() {
        List<BranchSummary> branches = new ArrayList<>();
        for (ProjectSummary project : getProjectList()) {
            branches.addAll(managementService.getBranchList(project.getId()));
        }
        return branches;
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/validation-stamp-mgt", method = RequestMethod.POST)
    public
    @ResponseBody
    Ack updateProjectValidationStamps(@PathVariable String project, @RequestBody ProjectValidationStampMgt form) {
        return managementService.updateProjectValidationStamps(
                entityConverter.getProjectId(project),
                form
        );
    }

    // Branches

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch", method = RequestMethod.GET)
    public
    @ResponseBody
    List<BranchSummary> getBranchList(@PathVariable String project) {
        int projectId = entityConverter.getProjectId(project);
        return managementService.getBranchList(projectId);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/status", method = RequestMethod.GET)
    public
    @ResponseBody
    List<BranchLastStatus> getBranchLastStatusList(final Locale locale, @PathVariable String project) {
        return Lists.transform(
                managementService.getBranchList(entityConverter.getProjectId(project)),
                new Function<BranchSummary, BranchLastStatus>() {
                    @Override
                    public BranchLastStatus apply(BranchSummary o) {
                        return managementService.getBranchLastStatus(locale, o.getId());
                    }
                }
        );
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{name:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.GET)
    public
    @ResponseBody
    BranchSummary getBranch(@PathVariable String project, @PathVariable String name) {
        int branchId = entityConverter.getBranchId(project, name);
        return managementService.getBranch(branchId);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{name:[A-Za-z0-9_\\.\\-]+}/status", method = RequestMethod.GET)
    public
    @ResponseBody
    BranchLastStatus getBranchLastStatus(Locale locale, @PathVariable String project, @PathVariable String name) {
        return managementService.getBranchLastStatus(locale, entityConverter.getBranchId(project, name));
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{name:[A-Za-z0-9_\\.\\-]+}/clone", method = RequestMethod.GET)
    public
    @ResponseBody
    BranchCloneInfo getBranchCloneInfo(Locale locale, @PathVariable String project, @PathVariable String name) {
        // Branch & project
        int projectId = entityConverter.getProjectId(project);
        int branchId = entityConverter.getBranchId(project, name);
        // Admin only
        securityUtils.checkGrant(ProjectFunction.BRANCH_CLONE, projectId);
        // Gets all the properties for the validation stamps
        Map<String, DisplayableProperty> validationStampIndex = new TreeMap<>();
        List<ValidationStampSummary> validationStampList = managementService.getValidationStampList(branchId);
        for (ValidationStampSummary validationStampSummary : validationStampList) {
            // Gets the list of properties for this validation stamp
            List<DisplayablePropertyValue> validationStampProperties = propertyUI.getProperties(locale, Entity.VALIDATION_STAMP, validationStampSummary.getId());
            for (DisplayablePropertyValue validationStampProperty : validationStampProperties) {
                String key = String.format("%s-%s", validationStampProperty.getExtension(), validationStampProperty.getName());
                validationStampIndex.put(
                        key,
                        toDisplayableProperty(validationStampProperty)
                );
            }
        }
        // Gets all the properties for the promotionlevels
        Map<String, DisplayableProperty> promotionLevelIndex = new TreeMap<>();
        List<PromotionLevelSummary> promotionLevelList = managementService.getPromotionLevelList(branchId);
        for (PromotionLevelSummary promotionLevelSummary : promotionLevelList) {
            // Gets the list of properties for this promotion level
            List<DisplayablePropertyValue> promotionLevelProperties = propertyUI.getProperties(locale, Entity.PROMOTION_LEVEL, promotionLevelSummary.getId());
            for (DisplayablePropertyValue promotionLevelProperty : promotionLevelProperties) {
                String key = String.format("%s-%s", promotionLevelProperty.getExtension(), promotionLevelProperty.getName());
                promotionLevelIndex.put(
                        key,
                        toDisplayableProperty(promotionLevelProperty)
                );
            }
        }
        // OK
        return new BranchCloneInfo(
                managementService.getBranch(branchId),
                Collections2.filter(
                        propertyUI.getEditableProperties(locale, Entity.BRANCH, branchId),
                        new Predicate<EditableProperty>() {
                            @Override
                            public boolean apply(EditableProperty property) {
                                return StringUtils.isNotBlank(property.getValue());
                            }
                        }
                ),
                validationStampIndex.values(),
                promotionLevelIndex.values()
        );
    }

    private DisplayableProperty toDisplayableProperty(DisplayablePropertyValue promotionLevelProperty) {
        return new DisplayableProperty(
                promotionLevelProperty.getExtension(),
                promotionLevelProperty.getName(),
                promotionLevelProperty.getDisplayName(),
                promotionLevelProperty.getIconPath()
        );
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{name:[A-Za-z0-9_\\.\\-]+}/decorated", method = RequestMethod.GET)
    public
    @ResponseBody
    DecoratedBranch getDecoratedBranch(Locale locale, @PathVariable String project, @PathVariable String name) {
        return managementService.getDecoratedBranch(locale, entityConverter.getBranchId(project, name));
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/filter", method = RequestMethod.GET)
    public
    @ResponseBody
    BranchFilterData getBranchFilterData(Locale locale, @PathVariable String project, @PathVariable String branch) {
        return new BranchFilterData(
                getPromotionLevelList(project, branch),
                getValidationStampList(project, branch),
                Arrays.asList(Status.values()),
                propertyUI.getPropertyList(locale, Entity.BUILD)
        );
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/cleanup", method = RequestMethod.GET)
    public
    @ResponseBody
    BuildCleanup getBuildCleanup(@PathVariable String project, @PathVariable String branch) {
        return managementService.getBuildCleanup(entityConverter.getBranchId(project, branch));
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/cleanup", method = RequestMethod.PUT)
    public
    @ResponseBody
    Ack setBuildCleanup(@PathVariable String project, @PathVariable String branch, @RequestBody BuildCleanupForm form) {
        return managementService.setBuildCleanup(entityConverter.getBranchId(project, branch), form);
    }

    @Override
    @RequestMapping(value = "/ui/manage/dashboard", method = RequestMethod.GET)
    @ResponseBody
    public List<DashboardConfig> getDashboards() {
        return dashboardService.getDashboardConfigs();
    }

    @Override
    @RequestMapping(value = "/ui/manage/dashboard", method = RequestMethod.POST)
    @ResponseBody
    public DashboardConfig createDashboard(@RequestBody DashboardConfigForm form) {
        return dashboardService.createDashboardConfig(form);
    }

    @Override
    @RequestMapping(value = "/ui/manage/dashboard/{id}", method = RequestMethod.GET)
    @ResponseBody
    public DashboardConfig getDashboard(@PathVariable int id) {
        return dashboardService.getDashboardConfig(id);
    }

    @Override
    @RequestMapping(value = "/ui/manage/dashboard/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public DashboardConfig updateDashboard(@PathVariable int id, @RequestBody DashboardConfigForm form) {
        return dashboardService.updateDashboardConfig(id, form);
    }

    @Override
    @RequestMapping(value = "/ui/manage/dashboard/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public Ack deleteDashboard(@PathVariable int id) {
        return dashboardService.deleteDashboardConfig(id);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch", method = RequestMethod.POST)
    public
    @ResponseBody
    BranchSummary createBranch(@PathVariable String project, @RequestBody BranchCreationForm form) {
        int projectId = entityConverter.getProjectId(project);
        return managementService.createBranch(projectId, form);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{name:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.PUT)
    public
    @ResponseBody
    BranchSummary updateBranch(@PathVariable String project, @PathVariable String name, @RequestBody BranchUpdateForm form) {
        return managementService.updateBranch(
                entityConverter.getBranchId(project, name),
                form
        );
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{name:[A-Za-z0-9_\\.\\-]+}/clone", method = RequestMethod.POST)
    public
    @ResponseBody
    BranchSummary cloneBranch(@PathVariable String project, @PathVariable String name, @RequestBody BranchCloneForm form) {
        return managementService.cloneBranch(
                entityConverter.getBranchId(project, name),
                form
        );
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{name:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    Ack deleteBranch(@PathVariable String project, @PathVariable String name) {
        int branchId = entityConverter.getBranchId(project, name);
        return managementService.deleteBranch(branchId);
    }

    // Validation stamps

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/validation_stamp", method = RequestMethod.GET)
    public
    @ResponseBody
    List<ValidationStampSummary> getValidationStampList(@PathVariable String project, @PathVariable String branch) {
        int branchId = entityConverter.getBranchId(project, branch);
        return managementService.getValidationStampList(branchId);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/validation_stamp/{name:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.GET)
    public
    @ResponseBody
    ValidationStampSummary getValidationStamp(@PathVariable String project, @PathVariable String branch, @PathVariable String name) {
        int validationStampId = entityConverter.getValidationStampId(project, branch, name);
        return managementService.getValidationStamp(validationStampId);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/validation_stamp/{validationStamp:[A-Za-z0-9_\\.\\-]+}/decorated", method = RequestMethod.GET)
    public
    @ResponseBody
    DecoratedValidationStamp getDecoratedValidationStamp(Locale locale, @PathVariable String project, @PathVariable String branch, @PathVariable String validationStamp) {
        int validationStampId = entityConverter.getValidationStampId(project, branch, validationStamp);
        return managementService.getDecoratedValidationStamp(locale, validationStampId);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/validation_stamp", method = RequestMethod.POST)
    public
    @ResponseBody
    ValidationStampSummary createValidationStamp(@PathVariable String project, @PathVariable String branch, @RequestBody ValidationStampCreationForm form) {
        int branchId = entityConverter.getBranchId(project, branch);
        return managementService.createValidationStamp(branchId, form);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/validation_stamp/{validationStamp:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.PUT)
    public
    @ResponseBody
    ValidationStampSummary updateValidationStamp(@PathVariable String project, @PathVariable String branch, @PathVariable String validationStamp, @RequestBody ValidationStampUpdateForm form) {
        return managementService.updateValidationStamp(
                entityConverter.getValidationStampId(project, branch, validationStamp),
                form
        );
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/validation_stamp/{name:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    Ack deleteValidationStamp(@PathVariable String project, @PathVariable String branch, @PathVariable String name) {
        int validationStampId = entityConverter.getValidationStampId(project, branch, name);
        return managementService.deleteValidationStamp(validationStampId);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/validation_stamp/{validationStamp:[A-Za-z0-9_\\.\\-]+}/comment", method = RequestMethod.POST)
    public
    @ResponseBody
    Ack addValidationStampComment(@PathVariable String project, @PathVariable String branch, @PathVariable String validationStamp, @RequestBody ValidationStampCommentForm form) {
        int validationStampId = entityConverter.getValidationStampId(project, branch, validationStamp);
        return managementService.addValidationStampComment(validationStampId, form);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/validation_stamp/{validationStamp:[A-Za-z0-9_\\.\\-]+}/comment", method = RequestMethod.GET)
    public
    @ResponseBody
    Collection<Comment> getValidationStampComments(Locale locale, @PathVariable String project, @PathVariable String branch, @PathVariable String validationStamp, @RequestParam(required = false, defaultValue = "0") int offset, @RequestParam(required = false, defaultValue = "10") int count) {
        return managementService.getValidationStampComments(locale, entityConverter.getValidationStampId(project, branch, validationStamp), offset, count);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/validation_stamp/{name:[A-Za-z0-9_\\.\\-]+}/image", method = RequestMethod.POST)
    public
    @ResponseBody
    Ack setImageValidationStamp(@PathVariable String project, @PathVariable String branch, @PathVariable String name, @RequestParam MultipartFile image) {
        int validationStampId = entityConverter.getValidationStampId(project, branch, name);
        return managementService.imageValidationStamp(validationStampId, image);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/validation_stamp/{name:[A-Za-z0-9_\\.\\-]+}/image", method = RequestMethod.GET)
    public
    @ResponseBody
    byte[] imageValidationStamp(@PathVariable String project, @PathVariable String branch, @PathVariable String name) {
        int validationStampId = entityConverter.getValidationStampId(project, branch, name);
        return managementService.imageValidationStamp(validationStampId);
    }

    // Promotion levels

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/promotion_level", method = RequestMethod.GET)
    public
    @ResponseBody
    List<PromotionLevelSummary> getPromotionLevelList(@PathVariable String project, @PathVariable String branch) {
        int branchId = entityConverter.getBranchId(project, branch);
        return managementService.getPromotionLevelList(branchId);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/promotion_level/{name:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.GET)
    public
    @ResponseBody
    PromotionLevelSummary getPromotionLevel(@PathVariable String project, @PathVariable String branch, @PathVariable String name) {
        int promotionLevelId = entityConverter.getPromotionLevelId(project, branch, name);
        return managementService.getPromotionLevel(promotionLevelId);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/promotion_level", method = RequestMethod.POST)
    public
    @ResponseBody
    PromotionLevelSummary createPromotionLevel(@PathVariable String project, @PathVariable String branch, @RequestBody PromotionLevelCreationForm form) {
        int branchId = entityConverter.getBranchId(project, branch);
        return managementService.createPromotionLevel(branchId, form);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/promotion_level/{promotionLevel:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.PUT)
    public
    @ResponseBody
    PromotionLevelSummary updatePromotionLevel(@PathVariable String project, @PathVariable String branch, @PathVariable String promotionLevel, @RequestBody PromotionLevelUpdateForm form) {
        return managementService.updatePromotionLevel(
                entityConverter.getPromotionLevelId(project, branch, promotionLevel),
                form
        );
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/promotion_level/{promotionLevel:[A-Za-z0-9_\\.\\-]+}/autopromote/set", method = RequestMethod.PUT)
    public
    @ResponseBody
    Flag setPromotionLevelAutoPromote(@PathVariable String project, @PathVariable String branch, @PathVariable String promotionLevel) {
        return managementService.setPromotionLevelAutoPromote(
                entityConverter.getPromotionLevelId(project, branch, promotionLevel)
        );
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/promotion_level/{promotionLevel:[A-Za-z0-9_\\.\\-]+}/autopromote/unset", method = RequestMethod.PUT)
    public
    @ResponseBody
    Flag unsetPromotionLevelAutoPromote(@PathVariable String project, @PathVariable String branch, @PathVariable String promotionLevel) {
        return managementService.unsetPromotionLevelAutoPromote(
                entityConverter.getPromotionLevelId(project, branch, promotionLevel)
        );
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/promotion_level/{name:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    Ack deletePromotionLevel(@PathVariable String project, @PathVariable String branch, @PathVariable String name) {
        int promotionLevelId = entityConverter.getPromotionLevelId(project, branch, name);
        return managementService.deletePromotionLevel(promotionLevelId);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/promotion_level/{name:[A-Za-z0-9_\\.\\-]+}/image", method = RequestMethod.POST)
    public
    @ResponseBody
    Ack setImagePromotionLevel(@PathVariable String project, @PathVariable String branch, @PathVariable String name, @RequestParam MultipartFile image) {
        int promotionLevelId = entityConverter.getPromotionLevelId(project, branch, name);
        return managementService.imagePromotionLevel(promotionLevelId, image);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/promotion_level/{name:[A-Za-z0-9_\\.\\-]+}/image", method = RequestMethod.GET)
    public
    @ResponseBody
    byte[] imagePromotionLevel(@PathVariable String project, @PathVariable String branch, @PathVariable String name) {
        int promotionLevelId = entityConverter.getPromotionLevelId(project, branch, name);
        return managementService.imagePromotionLevel(promotionLevelId);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/validation_stamp/{validationStamp:[A-Za-z0-9_\\.\\-]+}/link/{promotionLevel:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.GET)
    public
    @ResponseBody
    Ack linkValidationStampToPromotionLevel(@PathVariable String project, @PathVariable String branch, @PathVariable String validationStamp, @PathVariable String promotionLevel) {
        int validationStampId = entityConverter.getValidationStampId(project, branch, validationStamp);
        int promotionLevelId = entityConverter.getPromotionLevelId(project, branch, promotionLevel);
        return managementService.linkValidationStampToPromotionLevel(validationStampId, promotionLevelId);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/validation_stamp/{validationStamp:[A-Za-z0-9_\\.\\-]+}/unlink", method = RequestMethod.GET)
    public
    @ResponseBody
    Ack unlinkValidationStampToPromotionLevel(@PathVariable String project, @PathVariable String branch, @PathVariable String validationStamp) {
        int validationStampId = entityConverter.getValidationStampId(project, branch, validationStamp);
        return managementService.unlinkValidationStampToPromotionLevel(validationStampId);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/validation_stamp/{validationStamp:[A-Za-z0-9_\\.\\-]+}/up", method = RequestMethod.PUT)
    public
    @ResponseBody
    Ack upValidationStamp(@PathVariable String project, @PathVariable String branch, @PathVariable String validationStamp) {
        return managementService.upValidationStamp(entityConverter.getValidationStampId(project, branch, validationStamp));
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/validation_stamp/{validationStamp:[A-Za-z0-9_\\.\\-]+}/down", method = RequestMethod.PUT)
    public
    @ResponseBody
    Ack downValidationStamp(@PathVariable String project, @PathVariable String branch, @PathVariable String validationStamp) {
        return managementService.downValidationStamp(entityConverter.getValidationStampId(project, branch, validationStamp));
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/validation_stamp/{validationStamp:[A-Za-z0-9_\\.\\-]+}/move", method = RequestMethod.PUT)
    public
    @ResponseBody
    Ack moveValidationStamp(@PathVariable String project, @PathVariable String branch, @PathVariable String validationStamp, @RequestBody Reordering reordering) {
        return managementService.moveValidationStamp(entityConverter.getValidationStampId(project, branch, validationStamp), reordering.getNewIndex());
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/validation_stamp/{validationStamp:[A-Za-z0-9_\\.\\-]+}/owner/{ownerId:\\d+}", method = RequestMethod.PUT)
    public
    @ResponseBody
    Ack setValidationStampOwner(@PathVariable String project, @PathVariable String branch, @PathVariable String validationStamp, @PathVariable int ownerId) {
        return managementService.setValidationStampOwner(entityConverter.getValidationStampId(project, branch, validationStamp), ownerId);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/validation_stamp/{validationStamp:[A-Za-z0-9_\\.\\-]+}/owner", method = RequestMethod.DELETE)
    public
    @ResponseBody
    Ack unsetValidationStampOwner(@PathVariable String project, @PathVariable String branch, @PathVariable String validationStamp) {
        return managementService.unsetValidationStampOwner(entityConverter.getValidationStampId(project, branch, validationStamp));
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/promotion_level/{promotionLevel:[A-Za-z0-9_\\.\\-]+}/up", method = RequestMethod.GET)
    public
    @ResponseBody
    Ack upPromotionLevel(@PathVariable String project, @PathVariable String branch, @PathVariable String promotionLevel) {
        return managementService.upPromotionLevel(entityConverter.getPromotionLevelId(project, branch, promotionLevel));
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/promotion_level/{promotionLevel:[A-Za-z0-9_\\.\\-]+}/down", method = RequestMethod.GET)
    public
    @ResponseBody
    Ack downPromotionLevel(@PathVariable String project, @PathVariable String branch, @PathVariable String promotionLevel) {
        return managementService.downPromotionLevel(entityConverter.getPromotionLevelId(project, branch, promotionLevel));
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/promotion_level_manage", method = RequestMethod.GET)
    public
    @ResponseBody
    PromotionLevelManagementData getPromotionLevelManagementData(@PathVariable String project, @PathVariable String branch) {
        int branchId = entityConverter.getBranchId(project, branch);
        return managementService.getPromotionLevelManagementData(branchId);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/promotion_level/{promotionLevel:[A-Za-z0-9_\\.\\-]+}/validation_stamps", method = RequestMethod.GET)
    public
    @ResponseBody
    PromotionLevelAndStamps getPromotionLevelValidationStamps(@PathVariable String project, @PathVariable String branch, @PathVariable String promotionLevel) {
        return managementService.getPromotionLevelValidationStamps(
                entityConverter.getPromotionLevelId(project, branch, promotionLevel)
        );
    }

    @Override
    public BranchBuilds getBuilds(Locale locale, String project, String branch, BuildFilter filter) {
        int branchId = entityConverter.getBranchId(project, branch);
        return managementService.queryBuilds(locale, branchId, filter);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/build/{build:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    Ack deleteBuild(@PathVariable String project, @PathVariable String branch, @PathVariable String build) {
        return managementService.deleteBuild(entityConverter.getBuildId(project, branch, build));
    }

    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/build", method = RequestMethod.POST)
    public
    @ResponseBody
    BranchBuilds getBuilds(HttpServletResponse response, Locale locale, @PathVariable String project, @PathVariable String branch, @RequestBody BuildFilter filter) throws IOException {
        // Performs the query
        BranchBuilds builds = getBuilds(locale, project, branch, filter);
        // Filters on validation stamps?
        int currentAccountId = securityUtils.getCurrentAccountId();
        if (currentAccountId > 0) {
            // Gets the branch ID
            int branchId = entityConverter.getBranchId(project, branch);
            // Gets the list of filtered validation IDs
            Set<Integer> filteredStampIds = profileService.getFilteredValidationStampIds(branchId);
            // Operating the filter
            builds = builds.filterStamps(filteredStampIds);
            // Gets the list of saved filters
            builds = builds.withSavedBuildFilters(profileService.getFilters(branchId));
        }
        // Setting the cookie for the filter
        CookieGenerator cookie = new CookieGenerator();
        cookie.setCookieMaxAge(365 * 24 * 60 * 60); // 1 year
        cookie.setCookieName(String.format("%s|%s|filter", project, branch));
        cookie.addCookie(response, objectMapper.writeValueAsString(filter));
        // OK
        return builds;
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/build/last", method = RequestMethod.GET)
    public
    @ResponseBody
    OptionalBuildSummary getLastBuild(@PathVariable String project, @PathVariable String branch) {
        int branchId = entityConverter.getBranchId(project, branch);
        return new OptionalBuildSummary(managementService.getLastBuild(branchId));
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/build/withValidationStamp/{validationStamp:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.GET)
    public
    @ResponseBody
    OptionalBuildSummary getLastBuildWithValidationStamp(Locale locale, @PathVariable String project, @PathVariable String branch, @PathVariable String validationStamp) {
        int validationStampId = entityConverter.getValidationStampId(project, branch, validationStamp);
        return new OptionalBuildSummary(managementService.findLastBuildWithValidationStamp(validationStampId, Collections.singleton(Status.PASSED)));
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/build/withPromotionLevel/{promotionLevel:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.GET)
    public
    @ResponseBody
    OptionalBuildSummary getLastBuildWithPromotionLevel(Locale locale, @PathVariable String project, @PathVariable String branch, @PathVariable String promotionLevel) {
        int promotionLevelId = entityConverter.getPromotionLevelId(project, branch, promotionLevel);
        return new OptionalBuildSummary(managementService.findLastBuildWithPromotionLevel(promotionLevelId));
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/build/{name:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.GET)
    public
    @ResponseBody
    BuildSummary getBuild(@PathVariable String project, @PathVariable String branch, @PathVariable String name) {
        int buildId = entityConverter.getBuildId(project, branch, name);
        return managementService.getBuild(buildId);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/build/{build:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.PUT)
    public
    @ResponseBody
    BuildSummary updateBuild(@PathVariable String project, @PathVariable String branch, @PathVariable String build, @RequestBody BranchUpdateForm form) {
        return managementService.updateBuild(
                entityConverter.getBuildId(project, branch, build),
                form
        );
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/build/{name:[A-Za-z0-9_\\.\\-]+}/validationStamps", method = RequestMethod.GET)
    public
    @ResponseBody
    List<BuildValidationStamp> getBuildValidationStamps(Locale locale, @PathVariable String project, @PathVariable String branch, @PathVariable String name) {
        int buildId = entityConverter.getBuildId(project, branch, name);
        return managementService.getBuildValidationStamps(locale, buildId);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/build/{name:[A-Za-z0-9_\\.\\-]+}/promotionLevels", method = RequestMethod.GET)
    public
    @ResponseBody
    List<BuildPromotionLevel> getBuildPromotionLevels(Locale locale, @PathVariable String project, @PathVariable String branch, @PathVariable String name) {
        int buildId = entityConverter.getBuildId(project, branch, name);
        return managementService.getBuildPromotionLevels(locale, buildId);
    }

    // Validation runs

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/build/{build:[A-Za-z0-9_\\.\\-]+}/validation_stamp/{validationStamp:[A-Za-z0-9_\\.\\-]+}/validation_run/{run:[0-9]+}", method = RequestMethod.GET)
    public
    @ResponseBody
    ValidationRunSummary getValidationRun(@PathVariable String project, @PathVariable String branch, @PathVariable String build, @PathVariable String validationStamp, @PathVariable int run) {
        int runId = entityConverter.getValidationRunId(project, branch, build, validationStamp, run);
        return managementService.getValidationRun(runId);
    }

    @Override
    @RequestMapping(value = "/ui/manage/validation_run/{validationRunId:[0-9]+}/history", method = RequestMethod.GET)
    public
    @ResponseBody
    List<ValidationRunEvent> getValidationRunHistory(
            Locale locale,
            @PathVariable int validationRunId,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int count) {
        return managementService.getValidationRunHistory(locale, validationRunId, offset, count);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/validation_stamp/{validationStamp:[A-Za-z0-9_\\.\\-]+}/validation_run", method = RequestMethod.GET)
    public
    @ResponseBody
    List<ValidationRunEvent> getValidationRunsForValidationStamp(Locale locale, @PathVariable String project, @PathVariable String branch, @PathVariable String validationStamp, @RequestParam(required = false, defaultValue = "0") int offset, @RequestParam(required = false, defaultValue = "10") int count) {
        return managementService.getValidationRunsForValidationStamp(locale, entityConverter.getValidationStampId(project, branch, validationStamp), offset, count);
    }

    @Override
    @RequestMapping(value = "/ui/manage/validation_run/{runId:[0-9]+}/comment", method = RequestMethod.POST)
    public
    @ResponseBody
    Ack addValidationRunComment(@PathVariable int runId, @RequestBody ValidationRunCommentCreationForm form) {
        return managementService.addValidationRunComment(runId, form);
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/build/{build:[A-Za-z0-9_\\.\\-]+}/validation_stamp/{validationStamp:[A-Za-z0-9_\\.\\-]+}/validation_run/{runOrder:[0-9]+}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    Ack deleteValidationRun(@PathVariable String project, @PathVariable String branch, @PathVariable String build, @PathVariable String validationStamp, @PathVariable int runOrder) {
        return managementService.deleteValidationRun(entityConverter.getValidationRunId(
                project, branch, build, validationStamp, runOrder
        ));
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/promotion_level/{promotionLevel:[A-Za-z0-9_\\.\\-]+}/promotions", method = RequestMethod.GET)
    public
    @ResponseBody
    List<Promotion> getPromotions(Locale locale,
                                  @PathVariable String project,
                                  @PathVariable String branch,
                                  @PathVariable String promotionLevel,
                                  @RequestParam(required = false, defaultValue = "0") int offset,
                                  @RequestParam(required = false, defaultValue = "10") int count) {
        return managementService.getPromotions(
                locale,
                entityConverter.getPromotionLevelId(project, branch, promotionLevel),
                offset,
                count
        );
    }

    @Override
    @RequestMapping(value = "/ui/manage/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/build/{build:[A-Za-z0-9_\\.\\-]+}/promotion_level/{promotionLevel:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    Ack removePromotedRun(@PathVariable String project, @PathVariable String branch, @PathVariable String build, @PathVariable String promotionLevel) {
        int buildId = entityConverter.getBuildId(project, branch, build);
        int promotionLevelId = entityConverter.getPromotionLevelId(project, branch, promotionLevel);
        return managementService.removePromotedRun(buildId, promotionLevelId);
    }

    @RequestMapping(value = "/ui/manage/validation_run/{validationRunId:[0-9]+}/statusUpdateData", method = RequestMethod.GET)
    public
    @ResponseBody
    ValidationRunStatusUpdateData getValidationRunStatusUpdateData(Locale locale, @PathVariable int validationRunId) {
        // Gets the validation run
        ValidationRunSummary validationRun = managementService.getValidationRun(validationRunId);
        Status currentStatus = validationRun.getValidationRunStatus().getStatus();
        // Gets the properties for this run
        List<EditableProperty> editableProperties = propertyUI.getEditableProperties(locale, Entity.VALIDATION_RUN, validationRunId);
        // OK
        return new ValidationRunStatusUpdateData(
                Lists.newArrayList(currentStatus.getNext()),
                editableProperties
        );
    }
}
