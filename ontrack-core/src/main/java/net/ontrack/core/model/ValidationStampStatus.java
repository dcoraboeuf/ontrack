package net.ontrack.core.model;

import lombok.Data;

@Data
public class ValidationStampStatus {

    private final ValidationStampSummary stamp;
    private final ValidationRunStatusStub status;

}
