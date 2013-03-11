package net.ontrack.backend.dao.jdbc;

import net.ontrack.backend.dao.PromotionLevelDao;
import net.ontrack.backend.dao.model.TPromotionLevel;
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
public class PromotionLevelJdbcDao extends AbstractJdbcDao implements PromotionLevelDao {

    protected final RowMapper<TPromotionLevel> promotionLevelMapper = new RowMapper<TPromotionLevel>() {
        @Override
        public TPromotionLevel mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new TPromotionLevel(rs.getInt("id"), rs.getInt("branch"), rs.getInt("levelNb"), rs.getString("name"), rs.getString("description"));
        }
    };

    @Autowired
    public PromotionLevelJdbcDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TPromotionLevel> findByBranch(int branch) {
        return getNamedParameterJdbcTemplate().query(
                SQL.PROMOTION_LEVEL_LIST,
                params("branch", branch),
                promotionLevelMapper
        );
    }

    @Override
    @Transactional(readOnly = true)
    public TPromotionLevel getById(int id) {
        return getNamedParameterJdbcTemplate().queryForObject(
                SQL.PROMOTION_LEVEL,
                params("id", id),
                promotionLevelMapper);
    }

    @Override
    @Transactional
    public int createPromotionLevel(int branch, String name, String description) {
        // Count of existing promotion levels
        int count = getNamedParameterJdbcTemplate().queryForInt(
                SQL.PROMOTION_LEVEL_COUNT,
                params("branch", branch)
        );
        int levelNb = count + 1;
        // OK
        return dbCreate(
                SQL.PROMOTION_LEVEL_CREATE,
                params("branch", branch)
                        .addValue("name", name)
                        .addValue("description", description)
                        .addValue("levelNb", levelNb));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TPromotionLevel> findByBuild(int build) {
        return getNamedParameterJdbcTemplate().query(
                SQL.PROMOTION_LEVEL_FOR_BUILD,
                params("build", build),
                promotionLevelMapper
        );
    }
}
