package net.ontrack.core.model;

import lombok.Data;

@Data
public class ValidationRunEvent {

    private final DatedSignature signature;
    private final Status status;
    private final String content;

}
