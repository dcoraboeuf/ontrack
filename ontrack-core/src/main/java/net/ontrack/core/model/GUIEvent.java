package net.ontrack.core.model;

import lombok.Data;
import net.ontrack.core.model.EventType;

@Data
public class GUIEvent {
	
	private final int id;
	private final String author;
	private final EventType eventType;
	private final String timestamp;
	private final String elapsed;
	private final String html;
	private final String icon;
	private final String status;

}
