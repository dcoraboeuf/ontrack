package net.ontrack.web.api.assembly;

import com.google.common.base.Function;
import net.ontrack.core.model.ProjectSummary;
import net.ontrack.core.security.ProjectFunction;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.web.api.controller.ProjectController;
import net.ontrack.web.api.model.ProjectResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static net.ontrack.web.api.model.ResourceLink.of;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.http.HttpMethod.DELETE;

@Component
public class ProjectAssemblerImpl extends AbstractAssembler implements ProjectAssembler {

    @Autowired
    public ProjectAssemblerImpl(SecurityUtils securityUtils) {
        super(securityUtils);
    }

    @Override
    public Function<ProjectSummary, ProjectResource> summary() {
        return new Function<ProjectSummary, ProjectResource>() {
            @Override
            public ProjectResource apply(ProjectSummary o) {
                return new ProjectResource(
                        o.getId(),
                        o.getName(),
                        o.getDescription())
                        .withView("/project/%s", o.getName())
                        .withLink(linkTo(methodOn(ProjectController.class).getProject(o.getName())).withSelfRel())
                        .withLink(
                                of(DELETE, linkTo(methodOn(ProjectController.class).getProject(o.getName())).withRel("deleteProject")),
                                securityUtils.isGranted(ProjectFunction.PROJECT_DELETE, o.getId()))
                        ;
            }
        };
    }

    @Override
    public Function<ProjectSummary, ProjectResource> detail() {
        return new Function<ProjectSummary, ProjectResource>() {
            @Override
            public ProjectResource apply(ProjectSummary o) {
                return summary()
                        .apply(o)
                        // TODO Branches
                        // TODO Actions
                        ;
            }
        };
    }
}
