package net.ontrack.core.model;

import lombok.Data;

import java.util.Set;

@Data
public class BuildValidationStampFilter {

    private final String validationStamp;
    private final Set<Status> statuses;

}
