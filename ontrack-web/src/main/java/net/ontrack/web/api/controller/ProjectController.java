package net.ontrack.web.api.controller;

import net.ontrack.core.model.ProjectCreationForm;
import net.ontrack.core.security.GlobalFunction;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.core.support.EntityNameNotFoundException;
import net.ontrack.service.ManagementService;
import net.ontrack.web.api.input.ProjectCreation;
import net.ontrack.web.api.model.ProjectResource;
import net.ontrack.web.api.model.ResourceLink;
import net.ontrack.web.api.model.SimpleResourceCollection;
import net.ontrack.web.support.EntityConverter;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Controller
@RequestMapping("/api/project")
public class ProjectController extends APIController {

    private final SecurityUtils securityUtils;
    private final EntityConverter entityConverter;
    private final ManagementService managementService;

    @Autowired
    public ProjectController(ErrorHandler errorHandler, Strings strings, SecurityUtils securityUtils, EntityConverter entityConverter, ManagementService managementService) {
        super(errorHandler, strings);
        this.securityUtils = securityUtils;
        this.entityConverter = entityConverter;
        this.managementService = managementService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public SimpleResourceCollection<ProjectResource> getProjectList() {
        return SimpleResourceCollection.of(
                managementService.getProjectList(),
                ProjectResource.stubFn)
                .withLink(
                        ResourceLink.post(
                                linkTo(methodOn(ProjectController.class).createProject(null)).withRel("projectCreate")
                        ),
                        securityUtils.isGranted(GlobalFunction.PROJECT_CREATE)
                )
                ;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ProjectResource> createProject(@RequestBody ProjectCreation input) {
        return new ResponseEntity<>(
                ProjectResource.resourceFn
                        .apply(
                                managementService.createProject(
                                        new ProjectCreationForm(
                                                input.getName(),
                                                input.getDescription()
                                        )
                                )
                        ),
                HttpStatus.CREATED
        );
    }

    @RequestMapping(value = "/{name:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ProjectResource> getProject(@PathVariable String name) {
        try {
            return new ResponseEntity<>(
                    ProjectResource.resourceFn
                            .apply(
                                    managementService.getProject(
                                            entityConverter.getProjectId(name)
                                    )
                            ),
                    HttpStatus.OK
            );
        } catch (EntityNameNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}
