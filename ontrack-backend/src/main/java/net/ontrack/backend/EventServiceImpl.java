package net.ontrack.backend;

import static java.lang.String.format;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import net.ontrack.backend.db.SQL;
import net.ontrack.backend.db.SQLUtils;
import net.ontrack.core.model.*;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.service.EventService;
import net.ontrack.service.model.Event;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventServiceImpl extends NamedParameterJdbcDaoSupport implements EventService {

    private final SecurityUtils securityUtils;

    @Autowired
    public EventServiceImpl(DataSource dataSource, SecurityUtils securityUtils) {
        this.securityUtils = securityUtils;
        setDataSource(dataSource);
    }

    @Override
    @Transactional
    public void event(Event event) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        // Author
        Signature signature = securityUtils.getCurrentSignature();
        params.addValue("author", signature.getName());
        params.addValue("author_id", signature.getId());
        // Timestamping
        params.addValue("event_timestamp", SQLUtils.toTimestamp(SQLUtils.now()));
        // Event type
        params.addValue("event_type", event.getEventType().name());
        // SQL query
        StringBuilder sqlInsert = new StringBuilder("INSERT INTO EVENTS (AUTHOR, AUTHOR_ID, EVENT_TIMESTAMP, EVENT_TYPE");
        StringBuilder sqlValues = new StringBuilder("VALUES (:author, :author_id, :event_timestamp, :event_type");
        for (Map.Entry<Entity, Integer> entry : event.getEntities().entrySet()) {
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
        Map<String, String> values = event.getValues();
        params = new MapSqlParameterSource("id", eventId);
        for (Map.Entry<String, String> entry : values.entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue();
            getNamedParameterJdbcTemplate().update(SQL.EVENT_VALUE_INSERT, params.addValue("name", name).addValue("value", value));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpandedEvent> list(EventFilter filter) {
        // SQL
        StringBuilder sql = new StringBuilder("SELECT * FROM EVENTS");
        MapSqlParameterSource params = new MapSqlParameterSource();
        // Entities
        int count = 0;
        for (Map.Entry<Entity, Integer> entry : filter.getEntities().entrySet()) {
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
        params.addValue("offset", filter.getOffset());
        params.addValue("count", filter.getCount());
        // Query
        return getNamedParameterJdbcTemplate().query(
                sql.toString(),
                params,
                new RowMapper<ExpandedEvent>() {
                    @Override
                    public ExpandedEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return createAudit(rs);
                    }
                });
    }

    protected ExpandedEvent createAudit(ResultSet rs) throws SQLException {
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
            // Event
            ExpandedEvent e = new ExpandedEvent(id, author, eventType, timestamp);

            // Collects the entities
            for (Entity entity : Entity.values()) {
                int entityId = rs.getInt(entity.name());
                if (!rs.wasNull()) {
                    String entityName = getEntityName(entity, entityId);
                    e = e.withEntity(entity, new EntityStub(entity, entityId, entityName));
                }
            }

            // Collects the values
            List<Map<String, Object>> values = getNamedParameterJdbcTemplate().queryForList(SQL.EVENT_VALUE_LIST, new MapSqlParameterSource("id", id));
            for (Map<String, Object> row : values) {
                String name = (String) row.get("PROP_NAME");
                String value = (String) row.get("PROP_VALUE");
                e = e.withValue(name, value);
            }

            // OK
            return e;
        }
    }

    protected String getEntityName(Entity entity, int entityId) {
        return getNamedParameterJdbcTemplate().queryForObject(
                format(SQL.EVENT_NAME, entity.nameColumn(), entity.name()),
                new MapSqlParameterSource("id", entityId),
                String.class);

    }

}
