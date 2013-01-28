package net.ontrack.service;

import java.util.List;

import net.ontrack.service.model.Event;
import net.ontrack.service.model.EventType;

public interface EventService {

	void audit(EventType eventType, int id);

	List<Event> all(int offset, int count);

}
