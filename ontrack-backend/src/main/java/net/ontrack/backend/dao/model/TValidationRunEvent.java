package net.ontrack.backend.dao.model;

import lombok.Data;
import net.ontrack.core.model.Status;
import org.joda.time.DateTime;

@Data
public class TValidationRunEvent {

    private final Status status;
    private final String content;
    private final String author;
    private final Integer authorId;
    private final DateTime timestamp;

}
