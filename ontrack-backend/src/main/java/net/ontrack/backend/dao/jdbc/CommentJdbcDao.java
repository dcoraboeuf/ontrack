package net.ontrack.backend.dao.jdbc;

import net.ontrack.backend.dao.CommentDao;
import net.ontrack.backend.db.SQL;
import net.ontrack.core.model.Entity;
import net.ontrack.dao.AbstractJdbcDao;
import net.ontrack.dao.SQLUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

import static java.lang.String.format;

@Component
public class CommentJdbcDao extends AbstractJdbcDao implements CommentDao {

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
}
