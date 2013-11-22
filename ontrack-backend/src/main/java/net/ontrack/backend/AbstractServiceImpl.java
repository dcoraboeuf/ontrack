package net.ontrack.backend;

import com.google.common.base.Predicate;
import net.ontrack.core.model.DatedSignature;
import net.ontrack.core.model.Entity;
import net.ontrack.core.model.EventType;
import net.ontrack.service.EventService;
import net.ontrack.service.model.Event;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

public abstract class AbstractServiceImpl {

    private final ValidatorService validatorService;
    private final EventService eventService;

    public AbstractServiceImpl(ValidatorService validatorService, EventService eventService) {
        this.validatorService = validatorService;
        this.eventService = eventService;
    }

    protected void event(Event event) {
        eventService.event(event);
    }

    protected DatedSignature getDatedSignature(Locale locale, EventType eventType, Entity entity, int entityId) {
        return getDatedSignature(locale, eventType, Collections.singletonMap(entity, entityId));
    }

    protected DatedSignature getDatedSignature(Locale locale, EventType eventType, Map<Entity, Integer> entities) {
        return eventService.getDatedSignature(locale, eventType, entities);
    }

    public <T> void validate(T value, Predicate<T> predicate, String code, Object... parameters) {
        validatorService.validate(value, predicate, code, parameters);
    }

    protected void validate(final Object o, Class<?> group) {
        validatorService.validate(o, group);
    }

}
