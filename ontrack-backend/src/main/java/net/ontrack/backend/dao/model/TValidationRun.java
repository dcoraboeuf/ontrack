package net.ontrack.backend.dao.model;

import lombok.Data;

@Data
public class TValidationRun {

    private final int id;
    private final int build;
    private final int validationStamp;
    private final String description;
    private final int runOrder;

}
