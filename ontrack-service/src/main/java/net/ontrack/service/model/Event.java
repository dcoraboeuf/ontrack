package net.ontrack.service.model;

import lombok.Data;

import org.joda.time.DateTime;

@Data
public class Event {
	
	private final int id;
	// TODO Author info
	private final DateTime timestamp;
	private final EventType eventType;
	private final int sourceId;

}
