package net.ontrack.backend.dao.model;

import lombok.Data;
import org.joda.time.DateTime;

@Data
public class TComment {

    private final int id;
    private final String content;
    private final String author;
    private final Integer authorId;
    private final DateTime timestamp;

}
