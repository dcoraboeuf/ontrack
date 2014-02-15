package net.ontrack.extension.svn;

import net.ontrack.core.model.Ack;
import net.ontrack.core.model.UserMessage;
import net.ontrack.extension.svn.service.IndexationService;
import net.ontrack.extension.svn.service.model.LastRevisionInfo;
import net.ontrack.web.support.AbstractUIController;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
        // FIXME Indexation from latest
        if (indexationService.isIndexationRunning(0)) {
            return alreadyRunning(redirectAttributes);
        } else {
            indexationService.indexFromLatest(0);
            // Goes back to the home page
            return "redirect:/";
        }
    }

    @RequestMapping(value = "/range", method = RequestMethod.POST)
    public String range(@RequestParam long from, @RequestParam long to, RedirectAttributes redirectAttributes) {
        // FIXME Indexation of a range
        if (indexationService.isIndexationRunning(0)) {
            return alreadyRunning(redirectAttributes);
        } else {
            indexationService.indexRange(0, from, to);
            // Goes back to the home page
            return "redirect:/";
        }
    }

    @RequestMapping(value = "/{repositoryId}/full", method = RequestMethod.POST)
    @ResponseBody
    public Ack full(@PathVariable int repositoryId) {
        // Full indexation
        if (indexationService.isIndexationRunning(repositoryId)) {
            return Ack.NOK;
        } else {
            indexationService.reindex(repositoryId);
            return Ack.OK;
        }
    }

    private String alreadyRunning(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", UserMessage.error("subversion.indexation.alreadyrunning"));
        return "redirect:/gui/extension/svn/indexation";
    }
}
