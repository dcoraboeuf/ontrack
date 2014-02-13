package net.ontrack.extension.svn;

import net.ontrack.core.model.Ack;
import net.ontrack.extension.svn.service.SubversionService;
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
public class SVNConfigurationUIController extends AbstractUIController {

    private final SubversionService subversionService;

    @Autowired
    public SVNConfigurationUIController(ErrorHandler errorHandler, Strings strings, SubversionService subversionService) {
        super(errorHandler, strings);
        this.subversionService = subversionService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    public List<SVNRepository> getAllConfigurations() {
        return subversionService.getAllRepositories();
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public SVNRepository createConfiguration(@RequestBody SVNRepositoryForm configuration) {
        return subversionService.createRepository(configuration);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public SVNRepository updateConfiguration(@PathVariable int id, @RequestBody SVNRepositoryForm configuration) {
        return subversionService.updateRepository(id, configuration);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public SVNRepository getConfigurationById(@PathVariable int id) {
        return subversionService.getRepository(id);
    }

    // TODO Deletion configuration
    // @RequestMapping(value = "/{id}/deletion", method = RequestMethod.GET)
    // @ResponseBody
    // public JIRAConfigurationDeletion getConfigurationForDeletion(@PathVariable int id) {
    //     return jiraConfigurationService.getConfigurationForDeletion(id);
    // }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public Ack deleteConfiguration(@PathVariable int id) {
        return subversionService.deleteRepository(id);
    }
}
