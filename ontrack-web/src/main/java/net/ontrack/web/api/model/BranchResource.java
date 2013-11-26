package net.ontrack.web.api.model;

import lombok.Data;

@Data
public class BranchResource extends AbstractResource<BranchResource> {
    private final int id;
    private final String project;
    private final String name;
    private final String description;

}
