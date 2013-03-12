package net.ontrack.backend.dao.jdbc;

import net.ontrack.backend.dao.PromotedRunDao;
import net.ontrack.backend.dao.model.TPromotedRun;
import net.ontrack.backend.db.SQL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class PromotedRunJdbcDao extends AbstractJdbcDao implements PromotedRunDao {

    private final RowMapper<TPromotedRun> promotedRunRowMapper = new RowMapper<TPromotedRun>() {
        @Override
        public TPromotedRun mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new TPromotedRun(
                    rs.getInt("id"),
                    rs.getInt("build"),
                    rs.getInt("promotion_level"),
                    rs.getString("description")
            );
        }
    };

    @Autowired
    public PromotedRunJdbcDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    @Transactional(readOnly = true)
    public TPromotedRun findByBuildAndPromotionLevel(int build, int promotionLevel) {
        try {
            return getNamedParameterJdbcTemplate().queryForObject(
                    SQL.PROMOTED_RUN,
                    params("build", build).addValue("promotionLevel", promotionLevel),
                    promotedRunRowMapper
            );
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }
}
