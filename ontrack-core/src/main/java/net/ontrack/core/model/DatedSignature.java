package net.ontrack.core.model;

import lombok.Data;
import org.joda.time.DateTime;

@Data
public class
        DatedSignature {

    private final Signature signature;
    private final DateTime timestamp;
    private final String elapsedTime;
    private final String formattedTime;

}
