package net.ontrack.web.hateoas;

import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Controller
@RequestMapping("/rest")
public class RootResourceController extends AbstractResourceController {

    @Autowired
    public RootResourceController(ErrorHandler errorHandler, Strings strings) {
        super(errorHandler, strings);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public
    @ResponseBody
    ResourceSupport home() {
        ResourceSupport resource = new ResourceSupport();
        // Projects
        resource.add(linkTo(methodOn(ProjectResourceController.class).projectList()).withRel("projects"));
        // OK
        return resource;
    }

}
