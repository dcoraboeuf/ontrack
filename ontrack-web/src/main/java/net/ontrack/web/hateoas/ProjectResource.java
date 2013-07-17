package net.ontrack.web.hateoas;

import org.springframework.hateoas.ResourceSupport;

public class ProjectResource extends ResourceSupport {

    private final int projectId;
    private final String name;
    private final String description;

    public ProjectResource(int projectId, String name, String description) {
        this.projectId = projectId;
        this.name = name;
        this.description = description;
    }

    public int getProjectId() {
        return projectId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
