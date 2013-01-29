package net.ontrack.core.ui;

import java.util.List;

import net.ontrack.core.model.ExpandedEvent;


public interface EventUI {
	
	List<ExpandedEvent> all (int offset, int count);

}
