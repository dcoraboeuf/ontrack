package net.ontrack.service;

import net.ontrack.core.model.ExpandedEvent;
import net.ontrack.core.model.GUIEvent;
import org.joda.time.DateTime;

import java.util.Locale;

public interface GUIEventService {

    GUIEvent toGUIEvent(ExpandedEvent event, Locale locale, DateTime now);

}
