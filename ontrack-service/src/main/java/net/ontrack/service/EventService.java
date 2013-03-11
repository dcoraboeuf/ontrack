package net.ontrack.service;

import net.ontrack.core.model.*;
import net.ontrack.service.model.Event;

import java.util.List;
import java.util.Locale;

public interface EventService {

    void event(Event event);

    List<ExpandedEvent> list(EventFilter filter);

    String getEntityName(Entity entity, int entityId);

    DatedSignature getDatedSignature(Locale locale, EventType eventType, Entity entity, int entityId);
}
