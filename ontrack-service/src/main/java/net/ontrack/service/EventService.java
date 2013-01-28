package net.ontrack.service;

import java.util.List;

import net.ontrack.service.model.Event;
import net.ontrack.service.model.ExpandedEvent;

public interface EventService {

	void event(Event event);

	List<ExpandedEvent> all(int offset, int count);

}
