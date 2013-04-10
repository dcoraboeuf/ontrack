package net.ontrack.web.ui;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.ontrack.core.model.*;
import net.ontrack.core.support.TimeUtils;
import net.ontrack.core.ui.EventUI;
import net.ontrack.service.EventService;
import net.ontrack.service.GUIEventService;
import net.ontrack.web.support.AbstractUIController;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Strings;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/gui/event")
public class EventController extends AbstractUIController {

    private final EventUI eventUI;
    private final EventService auditService;
    private final GUIEventService guiEventService;

    @Autowired
    public EventController(ErrorHandler errorHandler, Strings strings, EventUI eventUI, EventService auditService, GUIEventService guiEventService) {
        super(errorHandler, strings);
        this.eventUI = eventUI;
        this.auditService = auditService;
        this.guiEventService = guiEventService;
    }

    @RequestMapping(value = "subscribe", method = RequestMethod.GET)
    public
    @ResponseBody
    Ack subscribe(
            @RequestParam(required = false, defaultValue = "0") int project,
            @RequestParam(required = false, defaultValue = "0") int branch,
            @RequestParam(required = false, defaultValue = "0") int validationStamp,
            @RequestParam(required = false, defaultValue = "0") int promotionLevel,
            @RequestParam(required = false, defaultValue = "0") int build,
            @RequestParam(required = false, defaultValue = "0") int validationRun) {
        return auditService.subscribe(
                getEventFilter(0, 0, project, branch, validationStamp, promotionLevel, build, validationRun));
    }

    @RequestMapping(value = "unsubscribe", method = RequestMethod.GET)
    public
    @ResponseBody
    Ack unsubscribe(
            @RequestParam(required = false, defaultValue = "0") int project,
            @RequestParam(required = false, defaultValue = "0") int branch,
            @RequestParam(required = false, defaultValue = "0") int validationStamp,
            @RequestParam(required = false, defaultValue = "0") int promotionLevel,
            @RequestParam(required = false, defaultValue = "0") int build,
            @RequestParam(required = false, defaultValue = "0") int validationRun) {
        return auditService.unsubscribe(
                getEventFilter(0, 0, project, branch, validationStamp, promotionLevel, build, validationRun));
    }

    @RequestMapping(method = RequestMethod.GET)
    public
    @ResponseBody
    List<GUIEvent> all(
            final Locale locale,
            @RequestParam(required = false, defaultValue = "0") int project,
            @RequestParam(required = false, defaultValue = "0") int branch,
            @RequestParam(required = false, defaultValue = "0") int validationStamp,
            @RequestParam(required = false, defaultValue = "0") int promotionLevel,
            @RequestParam(required = false, defaultValue = "0") int build,
            @RequestParam(required = false, defaultValue = "0") int validationRun,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "20") int count) {
        // Reference time
        final DateTime now = TimeUtils.now();
        // Filter
        EventFilter filter = getEventFilter(offset, count, project, branch, validationStamp, promotionLevel, build, validationRun);
        // Gets the raw events
        List<ExpandedEvent> events = eventUI.list(filter);
        // Localizes them
        // OK
        return Lists.transform(events, new Function<ExpandedEvent, GUIEvent>() {
            @Override
            public GUIEvent apply(ExpandedEvent event) {
                return toGUIEvent(event, locale, now);
            }
        });
    }

    private EventFilter getEventFilter(int offset, int count, int project, int branch, int validationStamp, int promotionLevel, int build, int validationRun) {
        EventFilter filter = new EventFilter(offset, count);
        filter.withEntity(Entity.PROJECT, project);
        filter.withEntity(Entity.BRANCH, branch);
        filter.withEntity(Entity.VALIDATION_STAMP, validationStamp);
        filter.withEntity(Entity.PROMOTION_LEVEL, promotionLevel);
        filter.withEntity(Entity.BUILD, build);
        filter.withEntity(Entity.VALIDATION_RUN, validationRun);
        return filter;
    }

    protected GUIEvent toGUIEvent(ExpandedEvent event, Locale locale, DateTime now) {
        // Call to the formatter
        return guiEventService.toGUIEvent(event, locale, now);
    }

}
