package net.ontrack.backend;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import net.ontrack.core.validation.ValidationException;
import net.sf.jstring.Localizable;
import net.sf.jstring.LocalizableMessage;
import net.sf.jstring.MultiLocalizable;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

public abstract class AbstractServiceImpl extends NamedParameterJdbcDaoSupport {

    private final Validator validator;
	
	public AbstractServiceImpl(DataSource dataSource, Validator validator) {
		setDataSource(dataSource);
        this.validator = validator;
	}
	
	protected <T> T getFirstItem (String sql, MapSqlParameterSource criteria, Class<T> type) {
		List<T> items = getNamedParameterJdbcTemplate().queryForList(sql, criteria, type);
		if (items.isEmpty()) {
			return null;
		} else {
			return items.get(0);
		}
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
