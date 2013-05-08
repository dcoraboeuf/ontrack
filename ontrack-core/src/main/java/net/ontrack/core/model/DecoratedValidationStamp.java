package net.ontrack.core.model;

import lombok.Data;

import java.util.List;

@Data
public class DecoratedValidationStamp {

    private final ValidationStampSummary summary;
    private final List<LocalizedDecoration> decorations;

}
