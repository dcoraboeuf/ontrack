package net.ontrack.web.api.controller;

import net.ontrack.web.api.controller.locale.ProductionAPILocalisationController;
import net.ontrack.web.api.model.TopResource;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Controller
@RequestMapping("/api")
public class TopController extends APIController {

    private final String version;

    @Autowired
    public TopController(ErrorHandler errorHandler, Strings strings, @Value("${app.version}") String version) {
        super(errorHandler, strings);
        this.version = version;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public TopResource top() throws IOException {
        return new TopResource(version)
                .withView("/home")
                .withLink(linkTo(methodOn(TopController.class).top()).withSelfRel())
                .withLink(linkTo(methodOn(ProjectController.class).getProjectList()).withRel("projectList"))
                .withLink(linkTo(methodOn(ProductionAPILocalisationController.class).localisationList()).withRel("languageList"))
                .withLink(linkTo(methodOn(ProductionAPILocalisationController.class).localisation("en", version)).withRel("localization"))
                ;
    }

}
