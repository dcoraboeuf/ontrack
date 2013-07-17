package net.ontrack.web.hateoas;

import net.ontrack.core.model.ProjectSummary;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class ProjectResourceAssembler extends ResourceAssemblerSupport<ProjectSummary, ProjectResource> {

    public ProjectResourceAssembler() {
        super(ProjectResourceController.class, ProjectResource.class);
    }

    @Override
    public ProjectResource toResource(ProjectSummary entity) {
        ProjectResource resource = createResourceWithId(entity.getId(), entity);
        resource.add(linkTo(methodOn(ProjectResourceController.class).projectBranchList(entity.getId())).withRel("branches"));
        return resource;
    }

    @Override
    protected ProjectResource instantiateResource(ProjectSummary entity) {
        return new ProjectResource(
                entity.getId(),
                entity.getName(),
                entity.getDescription()
        );
    }
}
