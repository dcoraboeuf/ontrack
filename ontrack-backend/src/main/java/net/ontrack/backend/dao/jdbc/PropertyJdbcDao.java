package net.ontrack.backend.dao.jdbc;

import net.ontrack.backend.dao.PropertyDao;
import net.ontrack.backend.db.SQL;
import net.ontrack.core.model.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

import static java.lang.String.format;

@Component
public class PropertyJdbcDao extends AbstractJdbcDao implements PropertyDao {

    @Autowired
    public PropertyJdbcDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    @Transactional
    public void saveProperty(Entity entity, int entityId, String extension, String name, String value) {
        // Deletes any previous value
        MapSqlParameterSource params = params("entityId", entityId)
                .addValue("extension", extension)
                .addValue("name", name)
                .addValue("value", value);
        getNamedParameterJdbcTemplate().update(
                format(SQL.PROPERTY_DELETE, entity.name()),
                params
        );
        // Inserts the value
        getNamedParameterJdbcTemplate().update(
                format(SQL.PROPERTY_INSERT, entity.name()),
                params
        );
    }
}
