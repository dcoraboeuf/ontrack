package net.ontrack.web.api.controller;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.ontrack.core.model.Ack;
import net.ontrack.core.model.BranchLastStatus;
import net.ontrack.core.model.BranchSummary;
import net.ontrack.core.model.ProjectCreationForm;
import net.ontrack.core.security.GlobalFunction;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.core.support.EntityNameNotFoundException;
import net.ontrack.service.ManagementService;
import net.ontrack.web.api.assembly.BranchLastStatusResourceAssembler;
import net.ontrack.web.api.assembly.ProjectAssembler;
import net.ontrack.web.api.input.ProjectCreation;
import net.ontrack.web.api.model.BranchLastStatusResource;
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

import java.util.List;
import java.util.Locale;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Controller
@RequestMapping("/api/project")
public class ProjectController extends APIController {

    private final SecurityUtils securityUtils;
    private final EntityConverter entityConverter;
    private final ManagementService managementService;
    private final ProjectAssembler projectAssembler;
    private final BranchLastStatusResourceAssembler branchLastStatusResourceAssembler;

    @Autowired
    public ProjectController(ErrorHandler errorHandler, Strings strings, SecurityUtils securityUtils, EntityConverter entityConverter, ManagementService managementService, ProjectAssembler projectAssembler, BranchLastStatusResourceAssembler branchLastStatusResourceAssembler) {
        super(errorHandler, strings);
        this.securityUtils = securityUtils;
        this.entityConverter = entityConverter;
        this.managementService = managementService;
        this.projectAssembler = projectAssembler;
        this.branchLastStatusResourceAssembler = branchLastStatusResourceAssembler;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public SimpleResourceCollection<ProjectResource> getProjectList() {
        return SimpleResourceCollection.of(
                managementService.getProjectList(),
                projectAssembler.summary())
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
                projectAssembler.detail()
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
                    projectAssembler.detail()
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

    @RequestMapping(value = "/{name:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.DELETE)
    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Ack deleteProject(@PathVariable String name) {
        return managementService.deleteProject(
                entityConverter.getProjectId(name)
        );
    }

    @RequestMapping(value = "/{name:[A-Za-z0-9_\\.\\-]+}/branch/status", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<SimpleResourceCollection<BranchLastStatusResource>> getProjectBranchStatusList(final Locale locale, @PathVariable String name) {
        try {
            // Project id
            int id = entityConverter.getProjectId(name);
            // Gets the branch statuses
            List<BranchLastStatus> statuses = Lists.transform(
                    managementService.getBranchList(id),
                    new Function<BranchSummary, BranchLastStatus>() {
                        @Override
                        public BranchLastStatus apply(BranchSummary o) {
                            return managementService.getBranchLastStatus(locale, o.getId());
                        }
                    }
            );
            // Getting the resources
            List<BranchLastStatusResource> statusResources = Lists.transform(
                    statuses,
                    branchLastStatusResourceAssembler.summary()
            );
            // OK
            return new ResponseEntity<>(new SimpleResourceCollection<>(statusResources), HttpStatus.OK);
        } catch (EntityNameNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}
