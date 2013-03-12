package net.ontrack.backend.dao.model;

import lombok.Data;
import net.ontrack.core.model.Status;
import org.joda.time.DateTime;

@Data
public class TValidationRunStatus {

    private final int id;
    private final int validationRun;
    private final Status status;
    private final String description;
    private final String author;
    private final Integer authorId;
    private final DateTime timestamp;

}
