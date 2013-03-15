package net.ontrack.backend.dao.jdbc;

import net.ontrack.backend.dao.PropertyDao;
import net.ontrack.backend.dao.model.TProperty;
import net.ontrack.backend.db.SQL;
import net.ontrack.core.model.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static java.lang.String.format;

@Component
public class PropertyJdbcDao extends AbstractJdbcDao implements PropertyDao {

    private final RowMapper<TProperty> propertyRowMapper = new RowMapper<TProperty>() {
        @Override
        public TProperty mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new TProperty(
                    rs.getInt("id"),
                    rs.getString("extension"),
                    rs.getString("name"),
                    rs.getString("value")
            );
        }
    };

    @Autowired
    public PropertyJdbcDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    @Transactional(readOnly = true)
    public TProperty findByExtensionAndName(Entity entity, int entityId, String extension, String name) {
        return getNamedParameterJdbcTemplate().queryForObject(
                format(SQL.PROPERTY_VALUE, entity.name()),
                params("entityId", entityId)
                        .addValue("extension", extension)
                        .addValue("name", name),
                propertyRowMapper
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TProperty> findAll(Entity entity, int entityId) {
        return getNamedParameterJdbcTemplate().query(
                format(SQL.PROPERTY_ALL, entity.name()),
                params("entityId", entityId),
                propertyRowMapper
        );
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
