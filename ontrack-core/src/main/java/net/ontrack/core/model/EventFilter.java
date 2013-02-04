package net.ontrack.core.model;

import java.util.HashMap;
import java.util.Map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class EventFilter {
	
	private final int offset;
	private final int count;

	private final Map<Entity, Integer> entities;
	
	public EventFilter(int offset, int count) {
		this(offset, count, new HashMap<Entity, Integer>());
	}
	
	public EventFilter withEntity (Entity entity, int id) {
		if (id > 0) {
			entities.put(entity, id);
		}
		return this;
	}

}
