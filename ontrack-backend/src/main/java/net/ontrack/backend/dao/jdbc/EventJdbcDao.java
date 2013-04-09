package net.ontrack.backend.dao.jdbc;

import net.ontrack.backend.EventNotRelatedException;
import net.ontrack.backend.dao.EventDao;
import net.ontrack.backend.dao.model.TEvent;
import net.ontrack.backend.db.SQL;
import net.ontrack.core.model.Entity;
import net.ontrack.core.model.EventType;
import net.ontrack.dao.AbstractJdbcDao;
import net.ontrack.dao.SQLUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

@Component
public class EventJdbcDao extends AbstractJdbcDao implements EventDao {

    private final RowMapper<TEvent> eventRowMapper = new RowMapper<TEvent>() {
        @Override
        public TEvent mapRow(ResultSet rs, int rowNum) throws SQLException {

            // General
            int id = rs.getInt("id");
            DateTime timestamp = SQLUtils.getDateTime(rs, "event_timestamp");
            // Author
            String author = rs.getString("author");
            // Event type
            EventType eventType = SQLUtils.getEnum(EventType.class, rs, "event_type");
            // Test of the source entity
            if (rs.wasNull()) {
                throw new EventNotRelatedException(id);
            } else {
                // Entity map
                Map<Entity, Integer> entityIds = new HashMap<>();
                // Value map
                Map<String, String> valuesMap = new HashMap<>();

                // Collects the entities
                for (Entity entity : Entity.values()) {
                    int entityId = rs.getInt(entity.name());
                    if (!rs.wasNull()) {
                        entityIds.put(entity, entityId);
                    }
                }

                // Collects the values
                List<Map<String, Object>> values = getNamedParameterJdbcTemplate().queryForList(SQL.EVENT_VALUE_LIST, new MapSqlParameterSource("id", id));
                for (Map<String, Object> row : values) {
                    String name = (String) row.get("PROP_NAME");
                    String value = (String) row.get("PROP_VALUE");
                    valuesMap.put(name, value);
                }

                // OK
                return new TEvent(
                        id,
                        author,
                        eventType,
                        timestamp,
                        entityIds,
                        valuesMap
                );
            }
        }
    };

    @Autowired
    public EventJdbcDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    @Transactional(readOnly = true)
    public TEvent getById(int id) {
        return getNamedParameterJdbcTemplate().queryForObject(
                SQL.EVENT,
                params("id", id),
                eventRowMapper
        );
    }

    @Override
    @Transactional
    public int createEvent(String author, Integer authorId, EventType eventType, Map<Entity, Integer> entities, Map<String, String> values) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        // Author
        params.addValue("author", author);
        params.addValue("author_id", authorId);
        // Timestamping
        params.addValue("event_timestamp", SQLUtils.toTimestamp(SQLUtils.now()));
        // Event type
        params.addValue("event_type", eventType.name());
        // SQL query
        StringBuilder sqlInsert = new StringBuilder("INSERT INTO EVENTS (AUTHOR, AUTHOR_ID, EVENT_TIMESTAMP, EVENT_TYPE");
        StringBuilder sqlValues = new StringBuilder("VALUES (:author, :author_id, :event_timestamp, :event_type");
        for (Map.Entry<Entity, Integer> entry : entities.entrySet()) {
            Entity entity = entry.getKey();
            int entityId = entry.getValue();
            sqlInsert.append(", ").append(entity.name());
            sqlValues.append(", :").append(entity.name());
            params.addValue(entity.name(), entityId);
        }
        String sql = sqlInsert + ") " + sqlValues + ")";
        // Execution
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        getNamedParameterJdbcTemplate().update(sql, params, keyHolder);
        int eventId = keyHolder.getKey().intValue();

        // Event values
        params = new MapSqlParameterSource("id", eventId);
        for (Map.Entry<String, String> entry : values.entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue();
            getNamedParameterJdbcTemplate().update(SQL.EVENT_VALUE_INSERT, params.addValue("name", name).addValue("value", value));
        }

        // OK
        return eventId;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TEvent> list(int offset, int limit, Map<Entity, Integer> entities) {
        // SQL
        StringBuilder sql = new StringBuilder("SELECT * FROM EVENTS");
        MapSqlParameterSource params = new MapSqlParameterSource();
        // Entities
        int count = 0;
        for (Map.Entry<Entity, Integer> entry : entities.entrySet()) {
            Entity entity = entry.getKey();
            int id = entry.getValue();
            if (count == 0) {
                sql.append(" WHERE");
            } else {
                sql.append(" AND");
            }
            count++;
            sql.append(format(" %1$s = :entity%1$s", entity.name()));
            params.addValue(format("entity%s", entity.name()), id);
        }
        // Limit & offset
        sql.append(" ORDER BY ID DESC LIMIT :count OFFSET :offset");
        params.addValue("offset", offset);
        params.addValue("count", limit);
        // Query
        return getNamedParameterJdbcTemplate().query(
                sql.toString(),
                params,
                eventRowMapper);
    }
}
