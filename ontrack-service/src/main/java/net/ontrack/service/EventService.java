package net.ontrack.service;

import net.ontrack.core.model.Entity;
import net.ontrack.core.model.EventFilter;
import net.ontrack.core.model.ExpandedEvent;
import net.ontrack.service.model.Event;

import java.util.List;

public interface EventService {

	void event(Event event);

	List<ExpandedEvent> list(EventFilter filter);

    String getEntityName(Entity entity, int entityId);
}
