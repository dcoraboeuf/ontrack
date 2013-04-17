package net.ontrack.web.gui;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import net.ontrack.core.model.SearchResult;
import net.ontrack.core.model.UserMessage;
import net.ontrack.core.support.InputException;
import net.ontrack.core.ui.ManageUI;
import net.ontrack.service.SearchService;
import net.ontrack.web.gui.model.GUISearchResult;
import net.ontrack.web.support.AbstractGUIController;
import net.ontrack.web.support.ErrorHandler;
import net.ontrack.web.support.ErrorHandlingMultipartResolver;
import net.sf.jstring.NonLocalizable;
import net.sf.jstring.Strings;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Locale;

@Controller
public class GUIController extends AbstractGUIController {

    private final ManageUI manageUI;
    private final ErrorHandlingMultipartResolver errorHandlingMultipartResolver;
    private final SearchService searchService;
    private final Strings strings;

    @Autowired
    public GUIController(ErrorHandler errorHandler, ManageUI manageUI, ErrorHandlingMultipartResolver errorHandlingMultipartResolver, SearchService searchService, Strings strings) {
        super(errorHandler);
        this.manageUI = manageUI;
        this.errorHandlingMultipartResolver = errorHandlingMultipartResolver;
        this.searchService = searchService;
        this.strings = strings;
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

    @RequestMapping(value = "/gui/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{name:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.GET)
    public String getBranch(Model model, @PathVariable String project, @PathVariable String name) {
        // Loads the details
        model.addAttribute("branch", manageUI.getBranch(project, name));
        // OK
        return "branch";
    }

    @RequestMapping(value = "/gui/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/build/{name:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.GET)
    public String getBuild(Model model, @PathVariable String project, @PathVariable String branch, @PathVariable String name) {
        // Loads the details
        model.addAttribute("build", manageUI.getBuild(project, branch, name));
        // OK
        return "build";
    }

    @RequestMapping(value = "/gui/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/validation_stamp/{name:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.GET)
    public String getValidationStamp(Model model, @PathVariable String project, @PathVariable String branch, @PathVariable String name) {
        // Loads the details
        model.addAttribute("validationStamp", manageUI.getValidationStamp(project, branch, name));
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
            // TODO Default image for promotion levels
            content = Base64.decodeBase64("iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAIAAACQd1PeAAAAE0lEQVR4XgXAAQ0AAABAMP1L38IF/gL+/AQ1bQAAAABJRU5ErkJggg==");
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
        return getValidationStamp(model, project, branch, name);
    }

    @RequestMapping(value = "/gui/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/validation_stamp/{name:[A-Za-z0-9_\\.\\-]+}/image", method = RequestMethod.GET)
    public void getImageValidationStamp(@PathVariable String project, @PathVariable String branch, @PathVariable String name, HttpServletResponse response) throws IOException {
        byte[] content = manageUI.imageValidationStamp(project, branch, name);
        if (content == null) {
            // TODO Default image for validation stamps
            content = Base64.decodeBase64("iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAIAAACQd1PeAAAAE0lEQVR4XgXAAQ0AAABAMP1L38IF/gL+/AQ1bQAAAABJRU5ErkJggg==");
        }
        renderImage(response, content);
    }

    /**
     * Search
     */
    @RequestMapping(value = "/gui/search", method = RequestMethod.GET)
    public String search(@RequestParam String token, Model model, final Locale locale) {
        // Fills the model with the search results
        Collection<SearchResult> results = searchService.search(token);
        // Gets the localization form
        Collection<GUISearchResult> guiResults = Collections2.transform(
                results,
                new Function<SearchResult, GUISearchResult>() {
                    @Override
                    public GUISearchResult apply(SearchResult result) {
                        return new GUISearchResult(
                                result.getTitle(),
                                result.getDescription().getLocalizedMessage(strings, locale),
                                result.getUrl()
                        );
                    }
                }
        );
        // FIXME One result only?
        // Adds into the model
        model.addAttribute("results", guiResults);
        // OK
        return "search";
    }

    protected void renderImage(HttpServletResponse response, byte[] content) throws IOException {
        response.setContentType("image/png");
        response.setContentLength(content.length);
        response.setStatus(HttpServletResponse.SC_OK);
        response.getOutputStream().write(content);
        response.getOutputStream().flush();
    }

}
