package net.ontrack.extension.svn;

import net.ontrack.core.model.UserMessage;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.extension.api.action.ActionExtension;
import net.ontrack.extension.svn.service.IndexationService;
import net.ontrack.extension.svn.service.model.LastRevisionInfo;
import net.ontrack.web.support.AbstractGUIController;
import net.ontrack.web.support.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/gui/extension/subversion/indexation")
public class IndexationActionController extends AbstractGUIController implements ActionExtension {

    private final IndexationService indexationService;

    @Autowired
    public IndexationActionController(ErrorHandler errorHandler, IndexationService indexationService) {
        super(errorHandler);
        this.indexationService = indexationService;
    }

    @Override
    public String getRole() {
        return SecurityRoles.ADMINISTRATOR;
    }

    @Override
    public String getExtension() {
        return SubversionExtension.EXTENSION;
    }

    @Override
    public String getName() {
        return "indexation";
    }

    @Override
    public String getPath() {
        return "gui/extension/subversion/indexation";
    }

    @Override
    public String getTitleKey() {
        return "subversion.indexation";
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String getPage(Model model) {
        // Gets the latest information
        LastRevisionInfo info = indexationService.getLastRevisionInfo();
        if (info != null) {
            model.addAttribute("lastRevisionInfo", info);
        }
        // OK
        return "extension/svn/indexation";
    }

    @RequestMapping(value = "/latest", method = RequestMethod.GET)
    public String latest(RedirectAttributes redirectAttributes) {
        // Indexation from latest
        if (indexationService.isIndexationRunning()) {
            return alreadyRunning(redirectAttributes);
        } else {
            indexationService.indexFromLatest();
            // Goes back to the home page
            return "redirect:/";
        }
    }

    @RequestMapping(value = "/range", method = RequestMethod.POST)
    public String range(@RequestParam long from, @RequestParam long to, RedirectAttributes redirectAttributes) {
        // Indexation of a range
        if (indexationService.isIndexationRunning()) {
            return alreadyRunning(redirectAttributes);
        } else {
            indexationService.indexRange(from, to);
            // Goes back to the home page
            return "redirect:/";
        }
    }

    @RequestMapping(value = "/full", method = RequestMethod.GET)
    public String full(RedirectAttributes redirectAttributes) {
        // Full indexation
        if (indexationService.isIndexationRunning()) {
            return alreadyRunning(redirectAttributes);
        } else {
            indexationService.reindex();
            // Goes back to the home page
            return "redirect:/";
        }
    }

    private String alreadyRunning(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", UserMessage.error("subversion.indexation.alreadyrunning"));
        return "redirect:/gui/extension/subversion/indexation";
    }
}
