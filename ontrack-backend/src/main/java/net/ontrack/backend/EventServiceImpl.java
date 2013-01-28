package net.ontrack.backend;

import static java.lang.String.format;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import net.ontrack.backend.db.SQL;
import net.ontrack.backend.db.SQLUtils;
import net.ontrack.service.EventService;
import net.ontrack.service.model.Event;
import net.ontrack.service.model.EventSource;
import net.ontrack.service.model.EventType;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventServiceImpl extends NamedParameterJdbcDaoSupport implements EventService {
	
	@Autowired
	public EventServiceImpl(DataSource dataSource) {
		setDataSource(dataSource);
	}

	@Override
	@Transactional
	public void audit(EventType eventType, int id) {
		MapSqlParameterSource params = new MapSqlParameterSource("id", id);
		params.addValue("author", "");
		params.addValue("author_id", null);
		params.addValue("event_timestamp", SQLUtils.toTimestamp(SQLUtils.now()));
		params.addValue("event_type", eventType.name());
		getNamedParameterJdbcTemplate().update(
			format(SQL.EVENT_CREATE, eventType.getSource().name()),
			params);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Event> all(int offset, int count) {
		return getNamedParameterJdbcTemplate().query(
			SQL.EVENT_ALL,
			new MapSqlParameterSource("offset", offset).addValue("count", count),
			new RowMapper<Event>() {
				@Override
				public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
					return createAudit(rs);
				}
			});
	}

	protected Event createAudit(ResultSet rs) throws SQLException {
		// General
		int id = rs.getInt("id");
		DateTime timestamp = SQLUtils.getDateTime(rs, "event_timestamp");
		boolean creation = rs.getBoolean("event_creation");
		// TODO Author
		// Event type
		EventType eventType = SQLUtils.getEnum(EventType.class, rs, "event_type");
		// Source entity
		int sourceId = rs.getInt(eventType.getSource().name());
		// Test of the source entity
		if (rs.wasNull()) {
			throw new EventNotRelatedException(id);
		} else {
			// OK
			return new Event(id, timestamp, creation, eventType, sourceId);
		}
	}

	protected String getAuditedName(EventSource audited, int auditedId) {
		return getNamedParameterJdbcTemplate().queryForObject(
			format(SQL.EVENT_NAME, audited.nameColumn(), audited.name()),
			new MapSqlParameterSource("id", auditedId),
			String.class);
			
	}

}
