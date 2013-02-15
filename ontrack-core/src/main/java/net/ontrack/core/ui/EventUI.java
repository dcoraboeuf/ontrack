package net.ontrack.core.ui;

import java.util.List;

import net.ontrack.core.model.EventFilter;
import net.ontrack.core.model.ExpandedEvent;


public interface EventUI {
	
	List<ExpandedEvent> list (EventFilter filter);

}
