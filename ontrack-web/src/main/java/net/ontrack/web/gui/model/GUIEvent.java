package net.ontrack.web.gui.model;

import lombok.Data;
import net.ontrack.core.model.EventType;

import org.joda.time.DateTime;

@Data
public class GUIEvent {
	
	private final int id;
	// TODO Author
	private final EventType eventType;
	private final DateTime timestamp;
	private final String html;

}
