package net.ontrack.service.model;

import java.util.HashMap;
import java.util.Map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.ontrack.core.model.Entity;
import net.ontrack.core.model.EventType;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Event {
	
	public static Event of (EventType eventType) {
		return new Event(eventType, new HashMap<Entity, Integer>(), new HashMap<String, String>()); 
	}
	
	private final EventType eventType;
	private final Map<Entity, Integer> entities;
	private final Map<String, String> values;
	
	public Event withValue (String name, String value) {
		values.put(name, value);
		return this;
	}
	
	public Event withProjectGroup (int id) {
		return withEntity (Entity.PROJECT_GROUP, id);
	}
	
	public Event withProject (int id) {
		return withEntity (Entity.PROJECT, id);
	}
	
	public Event withBranch (int id) {
		return withEntity (Entity.BRANCH, id);
	}

	private Event withEntity(Entity entity, int id) {
		entities.put(entity, id);
		return this;
	}

}
