package net.ontrack.backend.dao.jdbc;

import net.ontrack.backend.dao.SubscriptionDao;
import net.ontrack.backend.db.SQL;
import net.ontrack.core.model.Ack;
import net.ontrack.core.model.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

import static java.lang.String.format;

@Component
public class SubscriptionJdbcDao extends AbstractJdbcDao implements SubscriptionDao {

    @Autowired
    public SubscriptionJdbcDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    @Transactional
    public Ack subscribe(int userId, Entity entity, int entityId) {
        MapSqlParameterSource params = params("account", userId).addValue("entityId", entityId);
        // Deletion
        getNamedParameterJdbcTemplate().update(
                format(SQL.SUBSCRIPTION_DELETE, entity.name()),
                params
        );
        // Insertion
        return Ack.one(
                getNamedParameterJdbcTemplate().update(
                        format(SQL.SUBSCRIPTION_CREATE, entity.name()),
                        params)
        );
    }
}
