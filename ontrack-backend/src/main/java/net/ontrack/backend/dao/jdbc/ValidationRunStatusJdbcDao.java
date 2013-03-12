package net.ontrack.backend.dao.jdbc;

import net.ontrack.backend.dao.ValidationRunStatusDao;
import net.ontrack.backend.dao.model.TValidationRunStatus;
import net.ontrack.backend.db.SQL;
import net.ontrack.backend.db.SQLUtils;
import net.ontrack.core.model.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

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
}
