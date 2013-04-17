package net.ontrack.backend.dao.model;

import lombok.Data;
import net.ontrack.core.model.Entity;
import org.joda.time.DateTime;

import java.util.Map;

@Data
public class TComment {

    private final int id;
    private final String content;
    private final String author;
    private final Integer authorId;
    private final DateTime timestamp;
    private final Map<Entity, Integer> entities;

}
