package net.ontrack.backend.dao.jdbc;

import net.ontrack.backend.dao.ValidationRunStatusDao;
import net.ontrack.backend.dao.model.TValidationRunStatus;
import net.ontrack.backend.db.SQL;
import net.ontrack.core.model.Status;
import net.ontrack.dao.AbstractJdbcDao;
import net.ontrack.dao.SQLUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
public class ValidationRunStatusJdbcDao extends AbstractJdbcDao implements ValidationRunStatusDao {

    protected final RowMapper<TValidationRunStatus> validationRunStatusMapper = new RowMapper<TValidationRunStatus>() {
        @Override
        public TValidationRunStatus mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new TValidationRunStatus(
                    rs.getInt("id"),
                    rs.getInt("validation_run"),
                    SQLUtils.getEnum(Status.class, rs, "status"),
                    rs.getString("description"),
                    rs.getString("author"),
                    getInteger(rs, "author_id"),
                    SQLUtils.getDateTime(rs, "status_timestamp")
            );
        }
    };

    @Autowired
    public ValidationRunStatusJdbcDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    @Transactional(readOnly = true)
    public TValidationRunStatus findLastForValidationRun(int validationRunId) {
        return getNamedParameterJdbcTemplate().queryForObject(
                SQL.VALIDATION_RUN_STATUS_LAST,
                params("id", validationRunId),
                validationRunStatusMapper);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<TValidationRunStatus> findByText(String text) {
        return getNamedParameterJdbcTemplate().query(
                SQL.VALIDATION_RUN_STATUS_BY_NAME,
                params("text", "%" + StringUtils.upperCase(text) + "%"),
                validationRunStatusMapper
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<TValidationRunStatus> findByValidationRun(int validationRunId) {
        return getNamedParameterJdbcTemplate().query(
                SQL.VALIDATION_RUN_STATUS_BY_RUN,
                params("run", validationRunId),
                validationRunStatusMapper
        );
    }

    @Override
    @Transactional
    public void renameAuthor(int id, String name) {
        getNamedParameterJdbcTemplate().update(
                SQL.VALIDATION_RUN_STATUS_RENAME_AUTHOR,
                params("id", id).addValue("name", name)
        );
    }

    @Override
    @Transactional
    public int createValidationRunStatus(int validationRun, Status status, String description, String author, Integer authorId) {
        return dbCreate(
                SQL.VALIDATION_RUN_STATUS_CREATE,
                params("validationRun", validationRun)
                        .addValue("status", status.name())
                        .addValue("description", description)
                        .addValue("author", author)
                        .addValue("authorId", authorId)
                        .addValue("statusTimestamp", SQLUtils.toTimestamp(SQLUtils.now())));
    }

    @Override
    @Transactional
    public int createValidationRunStatusForImport(int validationRun, Status status, String description, String author, DateTime dateTime) {
        return dbCreate(
                SQL.VALIDATION_RUN_STATUS_CREATE,
                params("validationRun", validationRun)
                        .addValue("status", status.name())
                        .addValue("description", description)
                        .addValue("author", author)
                        .addValue("authorId", null)
                        .addValue("statusTimestamp", SQLUtils.toTimestamp(dateTime)));
    }
}
