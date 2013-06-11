package net.ontrack.core.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class FlaggedValidationStamp {

    private final ValidationStampSummary summary;
    private final boolean flag;

    public FlaggedValidationStamp(ValidationStampSummary summary) {
        this(summary, false);
    }

}
