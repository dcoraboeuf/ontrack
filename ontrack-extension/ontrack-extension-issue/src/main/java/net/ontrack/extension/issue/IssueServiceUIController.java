package net.ontrack.extension.issue;

import net.ontrack.web.support.AbstractUIController;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;

@Controller
@RequestMapping("/ui/extension/issue")
public class IssueServiceUIController extends AbstractUIController {

    private final IssueServiceFactory issueServiceFactory;

    @Autowired
    public IssueServiceUIController(ErrorHandler errorHandler, Strings strings, IssueServiceFactory issueServiceFactory) {
        super(errorHandler, strings);
        this.issueServiceFactory = issueServiceFactory;
    }

    /**
     * List of issue services.
     */
    @RequestMapping(value = "/service", method = RequestMethod.GET)
    @ResponseBody
    public Collection<IssueServiceSummary> getAllServices() {
        return issueServiceFactory.getAllServices();
    }

    /**
     * Returns the list of available configuration for a given service.
     */
    @RequestMapping(value = "/service/{serviceId}/configurations", method = RequestMethod.GET)
    @ResponseBody
    public Collection<IssueServiceConfigSummary> getAllConfigurations(@PathVariable String serviceId) {
        return issueServiceFactory.getServiceByName(serviceId).getAllConfigurations();
    }

}
