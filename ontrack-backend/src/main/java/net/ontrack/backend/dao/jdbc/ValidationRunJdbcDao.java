package net.ontrack.backend.dao.jdbc;

import net.ontrack.backend.dao.ValidationRunDao;
import net.ontrack.backend.dao.model.TValidationRun;
import net.ontrack.backend.db.SQL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class ValidationRunJdbcDao extends AbstractJdbcDao implements ValidationRunDao {

    private final RowMapper<TValidationRun> validationRunRowMapper = new RowMapper<TValidationRun>() {
        @Override
        public TValidationRun mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new TValidationRun(
                    rs.getInt("id"),
                    rs.getInt("build"),
                    rs.getInt("validation_stamp"),
                    rs.getString("description"),
                    rs.getInt("run_order")
            );
        }
    };

    @Autowired
    public ValidationRunJdbcDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    @Transactional(readOnly = true)
    public TValidationRun getById(int id) {
        return getNamedParameterJdbcTemplate().queryForObject(
                SQL.VALIDATION_RUN,
                params("id", id),
                validationRunRowMapper
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TValidationRun> findByBuildAndValidationStamp(int build, int validationStamp) {
        return getNamedParameterJdbcTemplate().query(
                SQL.VALIDATION_RUN_FOR_BUILD_AND_STAMP,
                params("build", build).addValue("validationStamp", validationStamp),
                validationRunRowMapper);
    }

    @Override
    @Transactional(readOnly = true)
    public TValidationRun findLastByBuildAndValidationStamp(int build, int validationStamp) {
        return getFirstItem(
                SQL.VALIDATION_RUN_LAST_FOR_BUILD_AND_STAMP,
                params("build", build).addValue("validationStamp", validationStamp),
                validationRunRowMapper);
    }

    @Override
    @Transactional
    public int createValidationRun(int build, int validationStamp, String description) {
        int count = getNamedParameterJdbcTemplate().queryForInt(
                SQL.VALIDATION_RUN_COUNT_FOR_BUILD_AND_STAMP,
                params("build", build).addValue("validationStamp", validationStamp)
        );
        return dbCreate(
                SQL.VALIDATION_RUN_CREATE,
                params("build", build)
                        .addValue("validationStamp", validationStamp)
                        .addValue("description", description)
                        .addValue("runOrder", count + 1));
    }
}
