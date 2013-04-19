package net.ontrack.backend.dao.jdbc;

import net.ontrack.backend.dao.PropertyDao;
import net.ontrack.backend.dao.model.TProperty;
import net.ontrack.backend.db.SQL;
import net.ontrack.core.model.Entity;
import net.ontrack.dao.AbstractJdbcDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
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
        try {
            return getNamedParameterJdbcTemplate().queryForObject(
                    format(SQL.PROPERTY_VALUE, entity.name()),
                    params("entityId", entityId)
                            .addValue("extension", extension)
                            .addValue("name", name),
                    propertyRowMapper
            );
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
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
    @Transactional(readOnly = true)
    public Collection<Integer> findEntityByPropertyValue(Entity entity, String extension, String name, String value) {
        return getNamedParameterJdbcTemplate().queryForList(
                format("SELECT %1$s FROM PROPERTIES WHERE %1$s IS NOT NULL AND EXTENSION = :extension AND NAME = :name AND VALUE = :value", entity.name()),
                params("extension", extension).addValue("name", name).addValue("value", value),
                Integer.class
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
        if (StringUtils.isNotBlank(value)) {
            getNamedParameterJdbcTemplate().update(
                    format(SQL.PROPERTY_INSERT, entity.name()),
                    params
            );
        }
    }
}
