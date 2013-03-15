package net.ontrack.backend.dao.jdbc;

import net.ontrack.backend.dao.BuildDao;
import net.ontrack.backend.dao.model.TBuild;
import net.ontrack.backend.db.SQL;
import net.ontrack.core.model.BuildFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class BuildJdbcDao extends AbstractJdbcDao implements BuildDao {

    private final RowMapper<TBuild> buildRowMapper = new RowMapper<TBuild>() {
        @Override
        public TBuild mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new TBuild(
                    rs.getInt("id"),
                    rs.getInt("branch"),
                    rs.getString("name"),
                    rs.getString("description")
            );
        }
    };

    @Autowired
    public BuildJdbcDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    @Transactional(readOnly = true)
    public TBuild getById(int id) {
        return getNamedParameterJdbcTemplate().queryForObject(
                SQL.BUILD,
                params("id", id),
                buildRowMapper
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TBuild> findByBranch(int branch, int offset, int count) {
        return getNamedParameterJdbcTemplate().query(
                SQL.BUILD_LIST,
                params("branch", branch).addValue("offset", offset).addValue("count", count),
                buildRowMapper);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TBuild> query(int branch, BuildFilter filter) {
        // Query root
        StringBuilder sql = new StringBuilder("SELECT B.* FROM BUILD B\n" +
                "LEFT JOIN PROMOTED_RUN PR ON PR.BUILD = B.ID\n" +
                "LEFT JOIN PROMOTION_LEVEL PL ON PL.ID = PR.PROMOTION_LEVEL\n" +
                "WHERE B.BRANCH = :branch");
        MapSqlParameterSource params = new MapSqlParameterSource("branch", branch);
        // TODO Since last promotion level
        // With promotion level
        String sincePromotionLevel = filter.getWithPromotionLevel();
        if (StringUtils.isNotBlank(sincePromotionLevel)) {
            sql.append(" AND PL.NAME = :sincePromotionLevel");
            params.addValue("sincePromotionLevel", sincePromotionLevel);
        }
        // TODO Since validation stamp
        // TODO With validation stamp
        // Ordering
        sql.append(" ORDER BY B.ID DESC");
        // Limit
        sql.append(" LIMIT :limit");
        params.addValue("limit", filter.getLimit());
        // OK
        return getNamedParameterJdbcTemplate().query(
                sql.toString(),
                params,
                buildRowMapper
        );
    }

    @Override
    @Transactional
    public int createBuild(int branch, String name, String description) {
        return dbCreate(
                SQL.BUILD_CREATE,
                params("branch", branch)
                        .addValue("name", name)
                        .addValue("description", description));
    }
}
