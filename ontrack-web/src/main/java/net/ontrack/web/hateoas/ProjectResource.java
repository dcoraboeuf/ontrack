package net.ontrack.web.hateoas;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.ontrack.core.model.ProjectSummary;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectResource extends AbstractResource<ProjectResource> {

    private final int projectId;
    private final String name;
    private final String description;

    public ProjectResource(ProjectSummary o) {
        this(o.getId(), o.getName(), o.getDescription());
    }
}
