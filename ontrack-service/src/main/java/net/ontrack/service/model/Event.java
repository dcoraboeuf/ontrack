package net.ontrack.service.model;

import java.util.HashMap;
import java.util.Map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Event {
	
	public static Event of (EventType eventType) {
		return new Event(eventType, new HashMap<Entity, Integer>()); 
	}
	
	private final EventType eventType;
	private final Map<Entity, Integer> entities;
	
	public Event withProjectGroup (int id) {
		return withEntity (Entity.PROJECT_GROUP, id);
	}
	
	public Event withProject (int id) {
		return withEntity (Entity.PROJECT, id);
	}

	private Event withEntity(Entity entity, int id) {
		entities.put(entity, id);
		return this;
	}

}
