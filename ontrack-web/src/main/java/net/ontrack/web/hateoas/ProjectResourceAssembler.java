package net.ontrack.web.hateoas;

import net.ontrack.core.model.ProjectSummary;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class ProjectResourceAssembler extends ResourceAssemblerSupport<ProjectSummary, ProjectResource> {

    public ProjectResourceAssembler() {
        super(ProjectResourceController.class, ProjectResource.class);
    }

    @Override
    public ProjectResource toResource(ProjectSummary entity) {
        return createResourceWithId(entity.getName(), entity);
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
