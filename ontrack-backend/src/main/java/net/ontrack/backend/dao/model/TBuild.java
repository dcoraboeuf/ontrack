package net.ontrack.backend.dao.model;

import lombok.Data;

@Data
public class TBuild {

    private final int id;
    private final int branch;
    private final String name;
    private final String description;

}
