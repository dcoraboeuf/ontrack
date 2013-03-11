package net.ontrack.backend;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import net.ontrack.core.model.Ack;
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
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.sql.DataSource;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.*;

public abstract class AbstractServiceImpl extends NamedParameterJdbcDaoSupport {

    private final Validator validator;
    private final EventService eventService;

    public AbstractServiceImpl(DataSource dataSource, Validator validator, EventService eventService) {
        setDataSource(dataSource);
        this.validator = validator;
        this.eventService = eventService;
    }

    protected void event(Event event) {
        eventService.event(event);
    }

    protected DatedSignature getDatedSignature (Locale locale, EventType eventType, Entity entity, int entityId) {
        return getDatedSignature (locale, eventType, Collections.singletonMap(entity, entityId));
    }

    protected DatedSignature getDatedSignature (Locale locale, EventType eventType, Map<Entity, Integer> entities) {
        return eventService.getDatedSignature (locale, eventType, entities);
    }

    protected <T> T getFirstItem(String sql, MapSqlParameterSource criteria, Class<T> type) {
        List<T> items = getNamedParameterJdbcTemplate().queryForList(sql, criteria, type);
        if (items.isEmpty()) {
            return null;
        } else {
            return items.get(0);
        }
    }

    protected int dbCreate(String sql, Map<String, ?> parameters) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        getNamedParameterJdbcTemplate().update(sql, new MapSqlParameterSource(parameters), keyHolder);
        return keyHolder.getKey().intValue();
    }

    protected Ack dbDelete(String sql, int id) {
        int count = getNamedParameterJdbcTemplate().update(sql, params("id", id));
        return Ack.one(count);
    }

    protected MapSqlParameterSource params(String name, Object value) {
        return new MapSqlParameterSource(name, value);
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
                    return getViolationMessage(o, violation);
                }
            });
            // Exception
            throw new ValidationException(new MultiLocalizable(messages));
        }
    }

    protected Localizable getViolationMessage(Object o, ConstraintViolation<Object> violation) {
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
