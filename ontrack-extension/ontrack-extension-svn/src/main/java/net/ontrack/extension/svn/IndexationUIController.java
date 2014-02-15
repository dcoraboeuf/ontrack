package net.ontrack.extension.svn;

import net.ontrack.core.model.Ack;
import net.ontrack.extension.svn.service.IndexationService;
import net.ontrack.extension.svn.service.model.LastRevisionInfo;
import net.ontrack.web.support.AbstractUIController;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(value = "/{repositoryId}/latest", method = RequestMethod.GET)
    @ResponseBody
    public Ack latest(@PathVariable int repositoryId) {
        if (indexationService.isIndexationRunning(repositoryId)) {
            return Ack.NOK;
        } else {
            indexationService.indexFromLatest(repositoryId);
            return Ack.OK;
        }
    }

    @RequestMapping(value = "/{repositoryId}/range", method = RequestMethod.POST)
    @ResponseBody
    public Ack range(@PathVariable int repositoryId, @RequestParam long from, @RequestParam long to) {
        if (indexationService.isIndexationRunning(repositoryId)) {
            return Ack.NOK;
        } else {
            indexationService.indexRange(repositoryId, from, to);
            return Ack.OK;
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
}
