package net.ontrack.extension.jira;

import net.ontrack.extension.jira.service.model.JIRAConfiguration;
import net.ontrack.web.support.AbstractUIController;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/ui/extension/jira/configuration")
public class JIRAConfigurationUIController extends AbstractUIController {

    private final JIRAConfigurationService jiraConfigurationService;

    @Autowired
    public JIRAConfigurationUIController(ErrorHandler errorHandler, Strings strings, JIRAConfigurationService jiraConfigurationService) {
        super(errorHandler, strings);
        this.jiraConfigurationService = jiraConfigurationService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    public List<JIRAConfiguration> getAllConfigurations() {
        return jiraConfigurationService.getAllConfigurations();
    }
}
