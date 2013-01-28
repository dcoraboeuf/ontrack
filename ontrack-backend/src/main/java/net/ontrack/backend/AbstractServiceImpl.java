package net.ontrack.backend;

import groovy.lang.Closure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import net.ontrack.core.validation.ValidationException;
import net.ontrack.service.EventService;
import net.ontrack.service.model.EventType;
import net.sf.jstring.Localizable;
import net.sf.jstring.LocalizableMessage;
import net.sf.jstring.MultiLocalizable;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

public abstract class AbstractServiceImpl extends NamedParameterJdbcDaoSupport {

    private final Validator validator;
    private final EventService auditService;
	
	public AbstractServiceImpl(DataSource dataSource, Validator validator, EventService auditService) {
		setDataSource(dataSource);
        this.validator = validator;
        this.auditService = auditService;
	}
	
	protected void audit (EventType eventType, int id) {
		auditService.audit(eventType, id);
	}
	
	protected <T> T getFirstItem (String sql, MapSqlParameterSource criteria, Class<T> type) {
		List<T> items = getNamedParameterJdbcTemplate().queryForList(sql, criteria, type);
		if (items.isEmpty()) {
			return null;
		} else {
			return items.get(0);
		}
	}
	
	protected <T> List<T> dbList (String sql, final Closure<T> mapping) {
		return getJdbcTemplate().query(sql, new RowMapper<T> () {

			@Override
			public T mapRow(ResultSet rs, int rowNum) throws SQLException {
				return mapping.call(rs);
			}
			
		});
	}
	
	protected <T> T dbLoad (String sql, int id, final Closure<T> mapping) {
		return getNamedParameterJdbcTemplate().queryForObject(
			sql,
			params("id", id),
			new RowMapper<T> () {

				@Override
				public T mapRow(ResultSet rs, int rowNum) throws SQLException {
					return mapping.call(rs);
				}
				
			}
		);
	}
	
	protected int dbCreate (String sql, Map<String, ?> parameters) {
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		getNamedParameterJdbcTemplate().update(sql, new MapSqlParameterSource(parameters), keyHolder);
		return keyHolder.getKey().intValue();
	}
	
	protected MapSqlParameterSource params (String name, Object value) {
		return new MapSqlParameterSource(name, value);
	}

    protected void validate (final Object o, Class<?> group) {
        Set<ConstraintViolation<Object>> violations = validator.validate(o, group);
        if (violations != null && !violations.isEmpty()) {
            Collection<Localizable> messages = Collections2.transform(violations, new Function<ConstraintViolation<Object>, Localizable>() {
                @Override
                public Localizable apply(ConstraintViolation<Object> violation) {
                    return getViolationMessage (o, violation);
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

}
