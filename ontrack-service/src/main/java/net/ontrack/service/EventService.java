package net.ontrack.service;

import net.ontrack.core.model.*;
import net.ontrack.service.model.Event;

import java.util.List;

public interface EventService {

    void event(Event event);

    List<ExpandedEvent> list(EventFilter filter);

    String getEntityName(Entity entity, int entityId);

    DatedSignature getDatedSignature(EventType eventType, Entity entity, int entityId);
}
