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
	private final String author;
	private final EventType eventType;
	private final DateTime timestamp;
	private final Map<Entity, EntityStub> entities;
	private final Map<String, String> values;

	public ExpandedEvent(int id, String author, EventType eventType, DateTime timestamp) {
		this(id, author, eventType, timestamp, new HashMap<Entity, EntityStub>(), new HashMap<String,String>());
	}
	
	public ExpandedEvent withValue (String name, String value) {
		values.put(name, value);
		return this;
	}
	
	public ExpandedEvent withEntity (Entity entity, EntityStub entityStub) {
		entities.put(entity, entityStub);
		return this;
	}

}
