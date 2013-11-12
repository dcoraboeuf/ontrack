package net.ontrack.web.api.model;

import com.google.common.base.Function;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.ontrack.core.model.ProjectSummary;
import net.ontrack.web.api.controller.ProjectController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Data
@EqualsAndHashCode(callSuper = false)
public class ProjectResource extends AbstractResource<ProjectResource> {

    private final int id;
    private final String name;
    private final String description;

    public static Function<ProjectSummary, ProjectResource> stubFn = new Function<ProjectSummary, ProjectResource>() {
        @Override
        public ProjectResource apply(ProjectSummary o) {
            return new ProjectResource(
                    o.getId(),
                    o.getName(),
                    o.getDescription())
                    .withView("/project/%s", o.getName())
                    .withLink(linkTo(methodOn(ProjectController.class).getProject(o.getName())).withSelfRel())
                    ;
        }
    };

    public static Function<ProjectSummary, ProjectResource> resourceFn = new Function<ProjectSummary, ProjectResource>() {
        @Override
        public ProjectResource apply(ProjectSummary o) {
            return stubFn
                    .apply(o)
                    // TODO Branches
                    // TODO Actions
                    ;
        }
    };

}
