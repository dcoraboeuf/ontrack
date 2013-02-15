package net.ontrack.service;

import java.util.List;

import net.ontrack.core.model.EventFilter;
import net.ontrack.core.model.ExpandedEvent;
import net.ontrack.service.model.Event;

public interface EventService {

	void event(Event event);

	List<ExpandedEvent> list(EventFilter filter);

}
