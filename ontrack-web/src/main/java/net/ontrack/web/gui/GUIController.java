package net.ontrack.web.gui;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import net.ontrack.core.model.ChartDefinition;
import net.ontrack.core.model.ExportData;
import net.ontrack.core.model.SearchResult;
import net.ontrack.core.model.UserMessage;
import net.ontrack.core.security.GlobalFunction;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.core.support.InputException;
import net.ontrack.core.ui.ManageUI;
import net.ontrack.service.DashboardService;
import net.ontrack.service.SearchService;
import net.ontrack.web.gui.model.GUISearchResult;
import net.ontrack.web.support.*;
import net.sf.jstring.NonLocalizable;
import net.sf.jstring.Strings;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

@Controller
public class GUIController extends AbstractGUIController {

    private final ManageUI manageUI;
    private final ErrorHandlingMultipartResolver errorHandlingMultipartResolver;
    private final EntityConverter entityConverter;
    private final SearchService searchService;
    private final DashboardService dashboardService;
    private final Strings strings;
    private final SecurityUtils securityUtils;
    private final ObjectMapper objectMapper;
    private final byte[] defaultValidationStampImage;
    private final byte[] defaultPromotionLevelImage;

    @Autowired
    public GUIController(ErrorHandler errorHandler, ManageUI manageUI, ErrorHandlingMultipartResolver errorHandlingMultipartResolver, EntityConverter entityConverter, SearchService searchService, DashboardService dashboardService, Strings strings, SecurityUtils securityUtils, ObjectMapper objectMapper) {
        super(errorHandler);
        this.manageUI = manageUI;
        this.errorHandlingMultipartResolver = errorHandlingMultipartResolver;
        this.entityConverter = entityConverter;
        this.searchService = searchService;
        this.dashboardService = dashboardService;
        this.strings = strings;
        this.securityUtils = securityUtils;
        this.objectMapper = objectMapper;
        // Reads the default images
        defaultValidationStampImage = WebUtils.readBytes("/default_validation_stamp.png");
        defaultPromotionLevelImage = WebUtils.readBytes("/default_promotion_level.png");
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home() {
        // OK
        return "home";
    }

    @RequestMapping(value = "/gui/project/{name:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.GET)
    public String getProject(Model model, @PathVariable String name) {
        // Loads the project details
        model.addAttribute("project", manageUI.getProject(name));
        // OK
        return "project";
    }

    @RequestMapping(value = "/gui/project/{name:[A-Za-z0-9_\\.\\-]+}/validation-stamp-mgt", method = RequestMethod.GET)
    public String manageProjectValidationStamps(Model model, @PathVariable String name) {
        securityUtils.checkGrant(GlobalFunction.PROJECT_CREATE);
        // Loads the project details
        model.addAttribute("project", manageUI.getProject(name));
        // OK
        return "project-validation-stamp-mgt";
    }

    @RequestMapping(value = "/gui/project/{name:[A-Za-z0-9_\\.\\-]+}/acl", method = RequestMethod.GET)
    public String manageProjectACL(Model model, @PathVariable String name) {
        // Loads the project details
        model.addAttribute("project", manageUI.getProject(name));
        // OK
        return "acl-project";
    }

    @RequestMapping(value = "/gui/import", method = RequestMethod.GET)
    public String importPage() {
        securityUtils.checkGrant(GlobalFunction.PROJECT_CREATE);
        return "import";
    }

    @RequestMapping(value = "/gui/import", method = RequestMethod.POST)
    public String importFile(HttpServletRequest request, Model model) {
        securityUtils.checkGrant(GlobalFunction.PROJECT_CREATE);
        // Error handling
        errorHandlingMultipartResolver.checkForUploadError(request);
        // Gets the file
        MultipartFile file = ((MultipartHttpServletRequest) request).getFile("file");
        if (file == null) {
            throw new IllegalStateException("Missing 'file' file parameter");
        }
        // Launches the import asynchronously
        String uid = manageUI.importLaunch(file).getUid();
        model.addAttribute("uid", uid);
        // OK
        return "import-feedback";
    }

    @RequestMapping(value = "/gui/export", method = RequestMethod.GET)
    public String exportPage() {
        securityUtils.checkGrant(GlobalFunction.PROJECT_EXPORT);
        return "export";
    }

    @RequestMapping(value = "/gui/project/{name:[A-Za-z0-9_\\.\\-]+}/export", method = RequestMethod.GET)
    public String exportProject(Model model, @PathVariable String name) {
        securityUtils.checkGrant(GlobalFunction.PROJECT_EXPORT);
        // Loads the project details
        model.addAttribute("project", manageUI.getProject(name));
        // Launching the export asynchronously
        model.addAttribute("projectExportUID", manageUI.exportProjectLaunch(name).getUid());
        // OK
        return "project-export";
    }

    @RequestMapping(value = "/gui/project/export/{uuid}", method = RequestMethod.GET)
    public void exportProjectDownload(@PathVariable String uuid, HttpServletResponse response) throws IOException {
        securityUtils.checkGrant(GlobalFunction.PROJECT_EXPORT);
        // Gets the file data
        ExportData data = manageUI.exportProjectDownload(uuid);
        // Headers
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.addHeader("Content-Disposition", "attachment; filename=ontrack-export.json");
        // Serializes as JSON
        objectMapper.writeValue(
                new OutputStreamWriter(
                        response.getOutputStream(),
                        "UTF-8"),
                data);
    }

    @RequestMapping(value = "/gui/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{name:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.GET)
    public String getBranch(Locale locale, Model model, @PathVariable String project, @PathVariable String name) {
        // Loads the details
        model.addAttribute("decoratedBranch", manageUI.getDecoratedBranch(locale, project, name));
        // OK
        return "branch";
    }

    @RequestMapping(value = "/gui/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{name:[A-Za-z0-9_\\.\\-]+}/charts", method = RequestMethod.GET)
    public String getBranchCharts(Model model, @PathVariable String project, @PathVariable String name) {
        // Loads the details
        model.addAttribute("branch", manageUI.getBranch(project, name));
        // All charts
        model.addAttribute("charts", Arrays.asList(
                new ChartDefinition("branch-validation-stamp-statuses"),
                new ChartDefinition("branch-validation-stamp-retries"),
                new ChartDefinition("branch-validation-stamp-runs-without-failure")
        ));
        // OK
        return "charts";
    }

    @RequestMapping(value = "/gui/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{name:[A-Za-z0-9_\\.\\-]+}/clone", method = RequestMethod.GET)
    public String cloneBranch(Locale locale, Model model, @PathVariable String project, @PathVariable String name) {
        // Loads the details
        model.addAttribute("branch", manageUI.getBranchCloneInfo(locale, project, name));
        // OK
        return "branchClone";
    }

    @RequestMapping(value = "/gui/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/build/{name:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.GET)
    public String getBuild(Model model, @PathVariable String project, @PathVariable String branch, @PathVariable String name) {
        // Loads the details
        model.addAttribute("build", manageUI.getBuild(project, branch, name));
        // OK
        return "build";
    }

    @RequestMapping(value = "/gui/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/validation_stamp/{name:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.GET)
    public String getValidationStamp(Locale locale, Model model, @PathVariable String project, @PathVariable String branch, @PathVariable String name) {
        // Loads the details
        model.addAttribute("decoratedValidationStamp", manageUI.getDecoratedValidationStamp(locale, project, branch, name));
        // OK
        return "validationStamp";
    }

    @RequestMapping(value = "/gui/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/promotion_level/{name:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.GET)
    public String getPromotionLevel(Model model, @PathVariable String project, @PathVariable String branch, @PathVariable String name) {
        // Loads the details
        model.addAttribute("promotionLevel", manageUI.getPromotionLevel(project, branch, name));
        // OK
        return "promotionLevel";
    }

    @RequestMapping(value = "/gui/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/promotion_level/{name:[A-Za-z0-9_\\.\\-]+}/image", method = RequestMethod.POST)
    public String imagePromotionLevel(HttpServletRequest request, Locale locale, Model model, @PathVariable String project, @PathVariable String branch, @PathVariable String name) {
        try {
            // Error handling
            errorHandlingMultipartResolver.checkForUploadError(request);
            // Gets the image
            MultipartFile image = ((MultipartHttpServletRequest) request).getFile("image");
            if (image == null) {
                throw new IllegalStateException("Missing 'image' file parameter");
            }
            // Upload
            manageUI.setImagePromotionLevel(project, branch, name, image);
            // Success
            model.addAttribute("message", UserMessage.success("promotion_level.image.success"));
        } catch (InputException ex) {
            // Error
            model.addAttribute("message", UserMessage.error(new NonLocalizable(errorHandler.displayableError(ex, locale))));
        }
        // OK
        return getPromotionLevel(model, project, branch, name);
    }

    @RequestMapping(value = "/gui/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/promotion_level/{name:[A-Za-z0-9_\\.\\-]+}/image", method = RequestMethod.GET)
    public void getImagePromotionLevel(@PathVariable String project, @PathVariable String branch, @PathVariable String name, HttpServletResponse response) throws IOException {
        byte[] content = manageUI.imagePromotionLevel(project, branch, name);
        if (content == null) {
            // Default image for promotion levels
            content = defaultPromotionLevelImage;
        }
        renderImage(response, content);
    }

    @RequestMapping(value = "/gui/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/promotion_level_manage", method = RequestMethod.GET)
    public String managePromotionLevels(@PathVariable String project, @PathVariable String branch, Model model) {
        model.addAttribute("management", manageUI.getPromotionLevelManagementData(project, branch));
        return "promotionLevelManagement";
    }

    @RequestMapping(value = "/gui/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/build/{build:[A-Za-z0-9_\\.\\-]+}/validation_stamp/{validationStamp:[A-Za-z0-9_\\.\\-]+}/validation_run/{run:[0-9]+}", method = RequestMethod.GET)
    public String getValidationRun(Model model, @PathVariable String project, @PathVariable String branch, @PathVariable String build, @PathVariable String validationStamp, @PathVariable int run) {
        // Loads the details
        model.addAttribute("validationRun", manageUI.getValidationRun(project, branch, build, validationStamp, run));
        // OK
        return "validationRun";
    }

    @RequestMapping(value = "/gui/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/validation_stamp/{name:[A-Za-z0-9_\\.\\-]+}/image", method = RequestMethod.POST)
    public String imageValidationStamp(HttpServletRequest request, Locale locale, Model model, @PathVariable String project, @PathVariable String branch, @PathVariable String name) {
        try {
            // Error handling
            errorHandlingMultipartResolver.checkForUploadError(request);
            // Gets the image
            MultipartFile image = ((MultipartHttpServletRequest) request).getFile("image");
            // Upload
            manageUI.setImageValidationStamp(project, branch, name, image);
            // Success
            model.addAttribute("message", UserMessage.success("validation_stamp.image.success"));
        } catch (InputException ex) {
            // Error
            model.addAttribute("message", UserMessage.error(new NonLocalizable(errorHandler.displayableError(ex, locale))));
        }
        // OK
        return getValidationStamp(locale, model, project, branch, name);
    }

    @RequestMapping(value = "/gui/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/validation_stamp/{name:[A-Za-z0-9_\\.\\-]+}/image", method = RequestMethod.GET)
    public void getImageValidationStamp(@PathVariable String project, @PathVariable String branch, @PathVariable String name, HttpServletResponse response) throws IOException {
        byte[] content = manageUI.imageValidationStamp(project, branch, name);
        if (content == null) {
            // Default image for validation stamps
            content = defaultValidationStampImage;
        }
        renderImage(response, content);
    }

    /**
     * Search
     */
    @RequestMapping(value = "/gui/search", method = RequestMethod.GET)
    public ModelAndView search(@RequestParam String token, final Locale locale) {
        // Normalization
        String searchToken = StringUtils.trimToEmpty(token);
        // Fills the model with the search results
        Collection<SearchResult> results = searchService.search(searchToken);
        // Gets the localization form
        Collection<GUISearchResult> guiResults = Collections2.transform(
                results,
                new Function<SearchResult, GUISearchResult>() {
                    @Override
                    public GUISearchResult apply(SearchResult result) {
                        return new GUISearchResult(
                                result.getTitle().getLocalizedMessage(strings, locale),
                                result.getDescription().getLocalizedMessage(strings, locale),
                                result.getUrl()
                        );
                    }
                }
        );
        // One result only?
        if (guiResults.size() == 1) {
            return new ModelAndView(new RedirectView(Iterables.get(guiResults, 0).getUrl(), false, false, false));
        } else {
            // OK
            return new ModelAndView("search", Collections.singletonMap("results", guiResults));
        }
    }

    /**
     * General dashboard
     */
    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public String generalDashboard(Locale locale, Model model) {
        model.addAttribute("dashboard", dashboardService.getGeneralDashboard(locale));
        return "dashboard";
    }

    /**
     * Project dashboard
     */
    @RequestMapping(value = "/dashboard/project/{project:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.GET)
    public String projectDashboard(Locale locale, Model model, @PathVariable String project) {
        model.addAttribute("dashboard", dashboardService.getProjectDashboard(locale, entityConverter.getProjectId(project)));
        return "dashboard";
    }

    /**
     * Branch dashboard
     */
    @RequestMapping(value = "/dashboard/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.GET)
    public String branchDashboard(Locale locale, Model model, @PathVariable String project, @PathVariable String branch) {
        model.addAttribute("dashboard", dashboardService.getBranchDashboard(locale, entityConverter.getBranchId(project, branch)));
        return "dashboard";
    }

    /**
     * Custom dashboard access
     */
    @RequestMapping(value = "/dashboard/{dashboard:\\d+}", method = RequestMethod.GET)
    public String customDashboard(Locale locale, Model model, @PathVariable int dashboard) {
        model.addAttribute("dashboard", dashboardService.getCustomDashboard(locale, dashboard));
        return "dashboard";
    }

    /**
     * Custom dashboard list
     */
    @RequestMapping(value = "/dashboard/custom", method = RequestMethod.GET)
    public String customDashboardList() {
        return "dashboard-custom";
    }

    protected void renderImage(HttpServletResponse response, byte[] content) throws IOException {
        // General
        response.setContentType("image/png");
        response.setContentLength(content.length);
        response.setStatus(HttpServletResponse.SC_OK);
        // See https://developers.google.com/speed/docs/best-practices/caching for cache management
        // Just sets the last modified time to now
        final long now = System.currentTimeMillis();
        response.setDateHeader("Expires", now + 24 * 3600L * 1000L); // Expires in one day
        response.setDateHeader("Last-Modified", now - 3600 * 1000); // Modified one hour ago
        // Content
        response.getOutputStream().write(content);
        response.getOutputStream().flush();
    }

}
