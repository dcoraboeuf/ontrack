package net.ontrack.core.model;

import java.util.HashMap;
import java.util.Map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import org.joda.time.DateTime;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ExpandedEvent {

	private final int id;
	// TODO Author
	private final EventType eventType;
	private final DateTime timestamp;
	private final Map<Entity, EntityStub> entities;

	public ExpandedEvent(int id, EventType eventType, DateTime timestamp) {
		this(id, eventType, timestamp, new HashMap<Entity, EntityStub>());
	}
	
	public ExpandedEvent withEntity (Entity entity, EntityStub entityStub) {
		entities.put(entity, entityStub);
		return this;
	}

}
