package net.ontrack.core.ui;

import net.ontrack.core.model.EventFilter;
import net.ontrack.core.model.ExpandedEvents;


public interface EventUI {

    ExpandedEvents list(EventFilter filter);

}
