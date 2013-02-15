package net.ontrack.service;

import net.ontrack.core.model.EventFilter;
import net.ontrack.core.model.ExpandedEvents;
import net.ontrack.service.model.Event;

public interface EventService {

    void event(Event event);

    ExpandedEvents list(EventFilter filter);

}
