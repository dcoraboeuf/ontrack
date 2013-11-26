package net.ontrack.web.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ProjectResource extends AbstractResource<ProjectResource> {

    private final int id;
    private final String name;
    private final String description;

}
