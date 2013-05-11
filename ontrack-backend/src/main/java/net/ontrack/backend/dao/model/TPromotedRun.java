package net.ontrack.backend.dao.model;

import lombok.Data;
import org.joda.time.DateTime;

@Data
public class TPromotedRun {

    private final int id;
    private final int build;
    private final int promotionLevel;
    private final Integer authorId;
    private final String author;
    private final DateTime creation;
    private final String description;

}
