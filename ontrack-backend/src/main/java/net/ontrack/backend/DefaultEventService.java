package net.ontrack.backend;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.ontrack.backend.dao.EntityDao;
import net.ontrack.backend.dao.EventDao;
import net.ontrack.backend.dao.model.TEvent;
import net.ontrack.core.model.*;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.core.support.TimeUtils;
import net.ontrack.dao.SQLUtils;
import net.ontrack.service.EventService;
import net.ontrack.service.SubscriptionService;
import net.ontrack.service.model.Event;
import net.sf.jstring.Strings;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.lang.String.format;

@Service
public class DefaultEventService extends NamedParameterJdbcDaoSupport implements EventService {

    private final SecurityUtils securityUtils;
    private final Strings strings;
    private final SubscriptionService subscriptionService;
    private final EventDao eventDao;
    private final EntityDao entityDao;
    private final Function<TEvent, ExpandedEvent> expandedEventFunction = new Function<TEvent, ExpandedEvent>() {
        @Override
        public ExpandedEvent apply(TEvent t) {
            return new ExpandedEvent(
                    t.getId(),
                    t.getAuthor(),
                    t.getEventType(),
                    t.getTimestamp(),
                    Maps.transformEntries(
                            t.getEntities(),
                            new Maps.EntryTransformer<Entity, Integer, EntityStub>() {
                                @Override
                                public EntityStub transformEntry(Entity entity, Integer entityId) {
                                    String entityName = getEntityName(entity, entityId);
                                    return new EntityStub(entity, entityId, entityName);
                                }
                            }
                    ),
                    t.getValues()
            );
        }
    };

    @Autowired
    public DefaultEventService(DataSource dataSource, SecurityUtils securityUtils, Strings strings, SubscriptionService subscriptionService, EventDao eventDao, EntityDao entityDao) {
        this.securityUtils = securityUtils;
        this.strings = strings;
        this.subscriptionService = subscriptionService;
        this.eventDao = eventDao;
        this.entityDao = entityDao;
        setDataSource(dataSource);
    }

    @Override
    @Transactional
    public Ack subscribe(EventFilter filter) {
        return subscriptionService.subscribe(filter.getEntities());
    }

    @Override
    @Transactional
    public Ack unsubscribe(EventFilter filter) {
        return subscriptionService.unsubscribe(filter.getEntities());
    }

    @Override
    @Transactional
    public void event(Event event) {
        Signature signature = securityUtils.getCurrentSignature();
        int eventId = eventDao.createEvent(
                signature.getName(),
                signature.getId(),
                event.getEventType(),
                event.getEntities(),
                event.getValues()
        );
        // Gets the expanded version for this event
        ExpandedEvent expandedEvent = expandedEventFunction.apply(
                eventDao.getById(eventId)
        );
        // Subscription
        subscriptionService.publish(expandedEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpandedEvent> list(EventFilter filter) {
        return Lists.transform(
                eventDao.list(
                        filter.getOffset(),
                        filter.getCount(),
                        filter.getEntities()
                ),
                expandedEventFunction
        );
    }

    @Override
    @Transactional(readOnly = true)
    public String getEntityName(Entity entity, int entityId) {
        return entityDao.getEntityName(entity, entityId);
    }

    @Override
    @Transactional(readOnly = true)
    public DatedSignature getDatedSignature(final Locale locale, EventType eventType, Map<Entity, Integer> entities) {
        StringBuilder sql = new StringBuilder("SELECT AUTHOR, AUTHOR_ID, EVENT_TIMESTAMP FROM EVENTS WHERE EVENT_TYPE = :eventType ");
        MapSqlParameterSource params = new MapSqlParameterSource("eventType", eventType.name());
        for (Map.Entry<Entity, Integer> entry : entities.entrySet()) {
            String entityName = entry.getKey().name();
            sql.append(format(" AND %1$s = :entry%1$s", entityName));
            params.addValue("entry" + entityName, entry.getValue());
        }
        sql.append(" ORDER BY ID DESC LIMIT 1");
        return getNamedParameterJdbcTemplate().queryForObject(
                sql.toString(),
                params,
                new RowMapper<DatedSignature>() {
                    @Override
                    public DatedSignature mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Integer id = rs.getInt("author_id");
                        if (rs.wasNull()) {
                            id = null;
                        }
                        DateTime timestamp = SQLUtils.getDateTime(rs, "event_timestamp");
                        String authorName = rs.getString("author");
                        return new DatedSignature(
                                new Signature(id, authorName),
                                timestamp,
                                TimeUtils.elapsed(strings, locale, timestamp, TimeUtils.now(), authorName),
                                TimeUtils.format(locale, timestamp)
                        );
                    }
                }
        );
    }
}
