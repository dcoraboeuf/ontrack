package net.ontrack.backend.dao.model;

import lombok.Data;

@Data
public class TBranch {

    private final int id;
    private final int project;
    private final String name;
    private final String description;

}
