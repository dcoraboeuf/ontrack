package net.ontrack.web.gui.model;

import lombok.Data;
import net.ontrack.core.model.EventType;

@Data
public class GUIEvent {
	
	private final int id;
	// TODO Author
	private final EventType eventType;
	private final String timestamp;
	private final String elapsed;
	private final String html;

}
