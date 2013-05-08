package net.ontrack.backend.dao.jdbc;

import net.ontrack.backend.dao.CommentDao;
import net.ontrack.backend.dao.model.TComment;
import net.ontrack.backend.db.SQL;
import net.ontrack.core.model.Entity;
import net.ontrack.dao.AbstractJdbcDao;
import net.ontrack.dao.SQLUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

@Component
public class CommentJdbcDao extends AbstractJdbcDao implements CommentDao {

    private final RowMapper<TComment> commentRowMapper = new RowMapper<TComment>() {
        @Override
        public TComment mapRow(ResultSet rs, int rowNum) throws SQLException {
            Map<Entity, Integer> entities = new HashMap<>();
            for (Entity candidate : Entity.values()) {
                int candidateId = rs.getInt(candidate.name());
                if (!rs.wasNull()) {
                    entities.put(candidate, candidateId);
                }
            }
            return new TComment(
                    rs.getInt("id"),
                    rs.getString("content"),
                    rs.getString("author"),
                    getInteger(rs, "author_id"),
                    SQLUtils.getDateTime(rs, "comment_timestamp"),
                    entities
            );
        }
    };

    @Autowired
    public CommentJdbcDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    @Transactional
    public int createComment(Entity entity, int entityId, String content, String author, Integer authorId) {
        return dbCreate(format(SQL.COMMENT_CREATE, entity.name()),
                params("content", content)
                        .addValue("id", entityId)
                        .addValue("author", author)
                        .addValue("author_id", authorId)
                        .addValue("comment_timestamp", SQLUtils.toTimestamp(SQLUtils.now())));
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<TComment> findByEntityAndText(Entity entity, String text) {
        return getNamedParameterJdbcTemplate().query(
                format("SELECT * FROM COMMENT WHERE %s IS NOT NULL AND UPPER(CONTENT) LIKE :text", entity.name()),
                params("text", "%" + StringUtils.upperCase(text) + "%"),
                commentRowMapper);
    }

    @Override
    @Transactional(readOnly = true)
    public void renameAuthor(int id, String name) {
        getNamedParameterJdbcTemplate().update(
                SQL.COMMENT_RENAME_AUTHOR,
                params("id", id).addValue("name", name)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<TComment> findByEntity(Entity entity, int entityId) {
        return getNamedParameterJdbcTemplate().query(
                format("SELECT * FROM COMMENT WHERE %s = :id ORDER BY ID DESC", entity.name()),
                params("id", entityId),
                commentRowMapper
        );
    }
}
