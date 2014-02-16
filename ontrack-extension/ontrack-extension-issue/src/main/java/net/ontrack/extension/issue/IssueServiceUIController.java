package net.ontrack.extension.issue;

import net.ontrack.web.support.AbstractUIController;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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

    @RequestMapping(value = "/services", method = RequestMethod.GET)
    @ResponseBody
    public Collection<IssueServiceSummary> getAllServices() {
        return issueServiceFactory.getAllServices();
    }

}
