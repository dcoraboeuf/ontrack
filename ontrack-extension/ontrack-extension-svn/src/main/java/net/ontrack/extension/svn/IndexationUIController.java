package net.ontrack.extension.svn;

import net.ontrack.core.model.UserMessage;
import net.ontrack.core.security.AuthorizationPolicy;
import net.ontrack.core.security.GlobalFunction;
import net.ontrack.extension.api.action.TopActionExtension;
import net.ontrack.extension.svn.service.IndexationService;
import net.ontrack.extension.svn.service.model.LastRevisionInfo;
import net.ontrack.web.support.AbstractGUIController;
import net.ontrack.web.support.AbstractUIController;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ui/extension/svn/indexation")
public class IndexationUIController extends AbstractUIController {

    private final IndexationService indexationService;

    @Autowired
    public IndexationUIController(ErrorHandler errorHandler, Strings strings, IndexationService indexationService) {
        super(errorHandler, strings);
        this.indexationService = indexationService;
    }

    @RequestMapping(value = "/{repositoryId}", method = RequestMethod.GET)
    @ResponseBody
    public LastRevisionInfo getLastRevisionInfo(@PathVariable int repositoryId) {
        return indexationService.getLastRevisionInfo(repositoryId);
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
        return "redirect:/gui/extension/svn/indexation";
    }
}
