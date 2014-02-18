package net.ontrack.extension.svn;

import net.ontrack.core.model.Ack;
import net.ontrack.extension.svn.service.RepositoryService;
import net.ontrack.extension.svn.service.model.SVNRepository;
import net.ontrack.extension.svn.service.model.SVNRepositoryForm;
import net.ontrack.web.support.AbstractUIController;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/ui/extension/svn/configuration")
public class SVNRepositoryUIController extends AbstractUIController {

    private final RepositoryService repositoryService;

    @Autowired
    public SVNRepositoryUIController(ErrorHandler errorHandler, Strings strings, RepositoryService repositoryService) {
        super(errorHandler, strings);
        this.repositoryService = repositoryService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    public List<SVNRepository> getAllConfigurations() {
        return repositoryService.getAllRepositories();
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public SVNRepository createConfiguration(@RequestBody SVNRepositoryForm configuration) {
        return repositoryService.createRepository(configuration);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public SVNRepository updateConfiguration(@PathVariable int id, @RequestBody SVNRepositoryForm configuration) {
        return repositoryService.updateRepository(id, configuration);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public SVNRepository getConfigurationById(@PathVariable int id) {
        return repositoryService.getRepository(id);
    }

    // FIXME Deletion configuration
    // @RequestMapping(value = "/{id}/deletion", method = RequestMethod.GET)
    // @ResponseBody
    // public JIRAConfigurationDeletion getConfigurationForDeletion(@PathVariable int id) {
    //     return jiraConfigurationService.getConfigurationForDeletion(id);
    // }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public Ack deleteConfiguration(@PathVariable int id) {
        return repositoryService.deleteRepository(id);
    }
}
