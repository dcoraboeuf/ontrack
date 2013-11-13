package net.ontrack.web.api.controller;

import net.ontrack.web.api.model.TopResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Controller
@RequestMapping("/api")
public class TopController {

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public TopResource top() {
        return new TopResource()
                .withView("/home")
                .withLink(linkTo(methodOn(TopController.class).top()).withSelfRel())
                .withLink(linkTo(methodOn(ProjectController.class).getProjectList()).withRel("projectList"));
    }

}
