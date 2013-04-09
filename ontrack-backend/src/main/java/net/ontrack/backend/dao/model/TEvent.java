package net.ontrack.backend.dao.model;

import lombok.Data;
import net.ontrack.core.model.Entity;
import net.ontrack.core.model.EventType;
import org.joda.time.DateTime;

import java.util.Map;

@Data
public class TEvent {

    private final int id;
    private final String author;
    private final EventType eventType;
    private final DateTime timestamp;
    private final Map<Entity, Integer> entities;
    private final Map<String, String> values;
}
