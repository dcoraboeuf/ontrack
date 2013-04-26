package net.ontrack.core.model;

import lombok.Data;

@Data
public class FilteredValidationStamp {

    private final ValidationStampSummary stamp;
    private final boolean filtered;

}
