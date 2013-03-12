package net.ontrack.backend;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import net.ontrack.core.model.DatedSignature;
import net.ontrack.core.model.Entity;
import net.ontrack.core.model.EventType;
import net.ontrack.core.validation.ValidationException;
import net.ontrack.service.EventService;
import net.ontrack.service.model.Event;
import net.sf.jstring.Localizable;
import net.sf.jstring.LocalizableMessage;
import net.sf.jstring.MultiLocalizable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.*;

public abstract class AbstractServiceImpl extends NamedParameterJdbcDaoSupport {

    private final Validator validator;
    private final EventService eventService;

    public AbstractServiceImpl(Validator validator, EventService eventService) {
        this.validator = validator;
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
        if (!predicate.apply(value)) {
            throw new ValidationException(new MultiLocalizable(Collections.singletonList(new LocalizableMessage(code, parameters))));
        }
    }

    protected void validate(final Object o, Class<?> group) {
        Set<ConstraintViolation<Object>> violations = validator.validate(o, group);
        if (violations != null && !violations.isEmpty()) {
            Collection<Localizable> messages = Collections2.transform(violations, new Function<ConstraintViolation<Object>, Localizable>() {
                @Override
                public Localizable apply(ConstraintViolation<Object> violation) {
                    return getViolationMessage(violation);
                }
            });
            // Exception
            throw new ValidationException(new MultiLocalizable(messages));
        }
    }

    protected Localizable getViolationMessage(ConstraintViolation<Object> violation) {
        // Message code
        String code = String.format("%s.%s",
                violation.getRootBeanClass().getName(),
                violation.getPropertyPath());
        // Message returned by the validator
        Object oMessage;
        String message = violation.getMessage();
        if (StringUtils.startsWith(message, "{net.iteach")) {
            String key = StringUtils.strip(message, "{}");
            oMessage = new LocalizableMessage(key);
        } else {
            oMessage = message;
        }
        // Complete message
        return new LocalizableMessage("validation.field", new LocalizableMessage(code), oMessage);
    }

    protected String getEntityName(Entity entity, int id) {
        return eventService.getEntityName(entity, id);
    }

}
