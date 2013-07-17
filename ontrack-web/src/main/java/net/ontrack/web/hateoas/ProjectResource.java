package net.ontrack.web.hateoas;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.ResourceSupport;

@Data
@EqualsAndHashCode(callSuper = false)
public class ProjectResource extends ResourceSupport {

    private final int projectId;
    private final String name;
    private final String description;

}
