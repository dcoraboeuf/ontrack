package net.ontrack.backend.dao.jdbc;

import net.ontrack.backend.EntityNameNotFoundException;
import net.ontrack.backend.dao.EntityDao;
import net.ontrack.core.model.Entity;
import net.ontrack.core.support.Each;
import net.ontrack.core.support.ItemActionWithIndex;
import net.ontrack.dao.AbstractJdbcDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.Map;

import static java.lang.String.format;

@Component
public class EntityJdbcDao extends AbstractJdbcDao implements EntityDao {

    @Autowired
    public EntityJdbcDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    @Transactional(readOnly = true)
    public int getEntityId(Entity entity, String name, final Map<Entity, Integer> parentIds) {
        final StringBuilder sql = new StringBuilder(format(
                "SELECT ID FROM %s WHERE %s = :name",
                entity.name(),
                entity.nameColumn()));
        final MapSqlParameterSource sqlParams = params("name", name);
        Each.withIndex(entity.getParents(), new ItemActionWithIndex<Entity>() {
            @Override
            public void apply(Entity parent, int index) {
                Integer parentId = parentIds.get(parent);
                sql.append(" AND ").append(parent.name()).append(" = :parent").append(index);
                sqlParams.addValue("parent" + index, parentId);
            }
        });
        Integer id = getFirstItem(sql.toString(), sqlParams, Integer.class);
        if (id == null) {
            throw new EntityNameNotFoundException(entity, name);
        } else {
            return id;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getParentEntityId(Entity parentEntity, Entity entity, int entityId) {
        return getFirstItem(
                format("SELECT %s FROM %s WHERE ID = :id", parentEntity.name(), entity.name()),
                params("id", entityId),
                Integer.class
        );
    }
}
