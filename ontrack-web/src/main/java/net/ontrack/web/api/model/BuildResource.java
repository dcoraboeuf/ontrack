package net.ontrack.web.api.model;

import lombok.Data;

@Data
public class BuildResource extends AbstractResource<BuildResource> {

    private final int id;
    private final String project;
    private final String branch;
    private final String name;
    private final String description;

}
