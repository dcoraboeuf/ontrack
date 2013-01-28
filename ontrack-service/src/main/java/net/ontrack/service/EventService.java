package net.ontrack.service;

import java.util.List;

import net.ontrack.service.model.Event;
import net.ontrack.service.model.EventSource;

public interface EventService {

	void audit(boolean creation, EventSource audited, int id);

	List<Event> all(int offset, int count);

}
