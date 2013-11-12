package net.ontrack.web.api.model;

import com.google.common.base.Function;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.ontrack.core.model.ProjectSummary;

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
                    o.getDescription()
            ).withView("/project/%s", o.getName());
            // TODO 'self' link
        }
    };

}
