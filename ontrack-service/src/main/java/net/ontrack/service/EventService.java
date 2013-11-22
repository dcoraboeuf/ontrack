package net.ontrack.service;

import net.ontrack.core.model.*;
import net.ontrack.service.model.Event;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface EventService extends Runnable {

    void event(Event event);

    List<ExpandedEvent> list(EventFilter filter);

    DatedSignature getDatedSignature(Locale locale, EventType eventType, Map<Entity, Integer> entities);

    Ack subscribe(EventFilter filter);

    Ack unsubscribe(EventFilter eventFilter);
}
