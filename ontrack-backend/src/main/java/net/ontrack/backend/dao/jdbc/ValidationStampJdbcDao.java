package net.ontrack.backend.dao.jdbc;

import net.ontrack.backend.dao.ValidationStampDao;
import net.ontrack.backend.dao.model.TValidationStamp;
import net.ontrack.backend.db.SQL;
import net.ontrack.core.model.Ack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class ValidationStampJdbcDao extends AbstractJdbcDao implements ValidationStampDao {
    protected final RowMapper<TValidationStamp> validationStampMapper = new RowMapper<TValidationStamp>() {
        @Override
        public TValidationStamp mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new TValidationStamp(
                    rs.getInt("id"),
                    rs.getInt("branch"),
                    rs.getString("name"),
                    rs.getString("description"),
                    getInteger(rs, "promotion_level"));
        }
    };

    @Autowired
    public ValidationStampJdbcDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TValidationStamp> findByBranch(int branch) {
        return getNamedParameterJdbcTemplate().query(
                SQL.VALIDATION_STAMP_LIST,
                params("branch", branch),
                validationStampMapper);
    }

    @Override
    @Transactional(readOnly = true)
    public TValidationStamp getById(int id) {
        return getNamedParameterJdbcTemplate().queryForObject(
                SQL.VALIDATION_STAMP,
                params("id", id),
                validationStampMapper);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TValidationStamp> findByPromotionLevel(int promotionLevel) {
        return getNamedParameterJdbcTemplate().query(
                SQL.VALIDATION_STAMP_FOR_PROMOTION_LEVEL,
                params("promotionLevel", promotionLevel),
                validationStampMapper
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TValidationStamp> findByNoPromotionLevel(int branch) {
        return getNamedParameterJdbcTemplate().query(
                SQL.VALIDATION_STAMP_WITHOUT_PROMOTION_LEVEL,
                params("branch", branch),
                validationStampMapper
        );
    }

    @Override
    @Transactional
    public int createValidationStamp(int branch, String name, String description) {
        return dbCreate(
                SQL.VALIDATION_STAMP_CREATE,
                params("branch", branch).addValue("name", name).addValue("description", description)
        );
    }

    @Override
    @Transactional
    public Ack deleteValidationStamp(int id) {
        return Ack.one(
                getNamedParameterJdbcTemplate().update(
                        SQL.VALIDATION_STAMP_DELETE,
                        params("id", id)
                )
        );
    }

    @Override
    @Transactional
    public Ack linkValidationStampToPromotionLevel(int validationStampId, int promotionLevelId) {
        return Ack.one(getNamedParameterJdbcTemplate().update(
                SQL.VALIDATION_STAMP_PROMOTION_LEVEL,
                params("id", validationStampId).addValue("promotionLevel", promotionLevelId)
        ));
    }

    @Override
    @Transactional
    public Ack unlinkValidationStampToPromotionLevel(int validationStampId) {
        return Ack.one(getNamedParameterJdbcTemplate().update(
                SQL.VALIDATION_STAMP_PROMOTION_LEVEL,
                params("id", validationStampId).addValue("promotionLevel", null)
        ));
    }
}
