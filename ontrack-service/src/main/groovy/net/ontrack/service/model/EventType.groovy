package net.ontrack.service.model

import net.ontrack.service.model.EventSource

enum EventType {
	
	PROJECT_GROUP_CREATED(EventSource.PROJECT_GROUP),
	
	PROJECT_CREATED(EventSource.PROJECT);
	
	private final EventSource source;

	private EventType(EventSource source) {
		this.source = source;
	}
	
	public EventSource getSource () {
		return source;
	}

}
