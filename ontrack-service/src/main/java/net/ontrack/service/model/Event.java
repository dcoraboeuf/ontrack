package net.ontrack.service.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.ontrack.core.model.Entity;
import net.ontrack.core.model.EventType;

import java.util.HashMap;
import java.util.Map;

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
	
	public Event withBuild (int id) {
		return withEntity (Entity.BUILD, id);
	}
	
	public Event withValidationRun (int id) {
		return withEntity (Entity.VALIDATION_RUN, id);
	}
	
	public Event withValidationStamp (int id) {
		return withEntity (Entity.VALIDATION_STAMP, id);
	}

	public Event withEntity(Entity entity, int id) {
		entities.put(entity, id);
		return this;
	}

}
