package net.ontrack.backend.dao.jdbc;

import net.ontrack.backend.Caches;
import net.ontrack.backend.dao.BuildDao;
import net.ontrack.backend.dao.ValidationStampDao;
import net.ontrack.backend.dao.model.TBuild;
import net.ontrack.backend.dao.model.TValidationStamp;
import net.ontrack.backend.db.SQL;
import net.ontrack.core.model.BuildFilter;
import net.ontrack.core.model.BuildValidationStampFilter;
import net.ontrack.core.model.Status;
import net.ontrack.dao.AbstractJdbcDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;

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
    private final ValidationStampDao validationStampDao;

    @Autowired
    public BuildJdbcDao(DataSource dataSource, ValidationStampDao validationStampDao) {
        super(dataSource);
        this.validationStampDao = validationStampDao;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(Caches.BUILD)
    public TBuild getById(int id) {
        return getNamedParameterJdbcTemplate().queryForObject(
                SQL.BUILD,
                params("id", id),
                buildRowMapper
        );
    }

    @Override
    @Transactional(readOnly = true)
    public TBuild findLastByBranch(int branch) {
        return getFirstItem(
                SQL.BUILD_LAST_BY_BRANCH,
                params("branch", branch),
                buildRowMapper
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<TBuild> findByName(String name) {
        return getNamedParameterJdbcTemplate().query(
                SQL.BUILD_BY_NAME,
                params("name", name),
                buildRowMapper
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Integer findByBrandAndName(int branchId, String buildName) {
        return getFirstItem(
                SQL.BUILD_BY_BRANCH_AND_NAME,
                params("branch", branchId).addValue("name", buildName),
                Integer.class
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
        StringBuilder sql = new StringBuilder("SELECT DISTINCT(B.*) FROM BUILD B" +
                "                LEFT JOIN PROMOTED_RUN PR ON PR.BUILD = B.ID" +
                "                LEFT JOIN PROMOTION_LEVEL PL ON PL.ID = PR.PROMOTION_LEVEL" +
                "                LEFT JOIN (" +
                "                    SELECT R.BUILD,  R.VALIDATION_STAMP, VRS.STATUS " +
                "                    FROM VALIDATION_RUN R" +
                "                    INNER JOIN VALIDATION_RUN_STATUS VRS ON VRS.ID = (SELECT ID FROM VALIDATION_RUN_STATUS WHERE VALIDATION_RUN = R.ID ORDER BY ID DESC LIMIT 1)" +
                "                    AND R.RUN_ORDER = (SELECT MAX(RUN_ORDER) FROM VALIDATION_RUN WHERE BUILD = R.BUILD AND VALIDATION_STAMP = R.VALIDATION_STAMP)" +
                "                    ) S ON S.BUILD = B.ID" +
                "                WHERE B.BRANCH = :branch");
        MapSqlParameterSource params = new MapSqlParameterSource("branch", branch);
        Integer sinceBuildId = null;
        // Since last promotion level
        String sincePromotionLevel = filter.getSincePromotionLevel();
        if (StringUtils.isNotBlank(sincePromotionLevel)) {
            // Gets the last build having this promotion level
            TBuild build = findLastBuildWithPromotionLevel(branch, sincePromotionLevel);
            if (build != null) {
                sinceBuildId = build.getId();
            }
        }
        // With promotion level
        String withPromotionLevel = filter.getWithPromotionLevel();
        if (StringUtils.isNotBlank(withPromotionLevel)) {
            sql.append(" AND PL.NAME = :withPromotionLevel");
            params.addValue("withPromotionLevel", withPromotionLevel);
        }
        // Since validation stamp
        List<BuildValidationStampFilter> sinceValidationStamps = filter.getSinceValidationStamps();
        if (sinceValidationStamps != null && !sinceValidationStamps.isEmpty()) {
            for (BuildValidationStampFilter sinceValidationStamp : sinceValidationStamps) {
                // Gets the last build having this validation stamp and the status
                TBuild build = findLastBuildWithValidationStamp(branch, sinceValidationStamp.getValidationStamp(), sinceValidationStamp.getStatuses());
                if (build != null) {
                    if (sinceBuildId == null) {
                        sinceBuildId = build.getId();
                    } else {
                        sinceBuildId = Math.min(sinceBuildId.intValue(), build.getId());
                    }
                }
            }
        }
        // With validation stamp
        List<BuildValidationStampFilter> withValidationStamps = filter.getWithValidationStamps();
        if (withValidationStamps != null && !withValidationStamps.isEmpty()) {
            sql.append(" AND (");
            int index = 0;
            for (BuildValidationStampFilter stamp : withValidationStamps) {
                if (index > 0) {
                    sql.append(" OR ");
                }
                TValidationStamp tstamp = validationStampDao.getByBranchAndName(branch, stamp.getValidationStamp());
                sql.append(format("(S.VALIDATION_STAMP = :validationStamp%d", index));
                params.addValue(format("validationStamp%d", index), tstamp.getId());
                // Status criteria
                Set<Status> statuses = stamp.getStatuses();
                if (statuses != null && !statuses.isEmpty()) {
                    sql.append(format(" AND S.STATUS IN (%s)", getStatusesForSQLInClause(statuses)));
                }
                // OK for this validation stamp
                sql.append(")");
                index++;
            }
            sql.append(")");
        }
        // Since build?
        if (sinceBuildId != null) {
            sql.append(" AND B.ID >= :sinceBuildId");
            params.addValue("sinceBuildId", sinceBuildId);
        }
        // Ordering
        sql.append(" ORDER BY B.ID DESC");
        // Limit
        sql.append(" LIMIT :limit");
        params.addValue("limit", filter.getLimit());
        // List of builds
        return getNamedParameterJdbcTemplate().query(
                sql.toString(),
                params,
                buildRowMapper
        );
    }

    @Override
    public TBuild findLastBuildWithValidationStamp(int branch, String validationStamp, Set<Status> statuses) {
        int validationStampId = validationStampDao.getByBranchAndName(branch, validationStamp).getId();
        StringBuilder sql = new StringBuilder("SELECT DISTINCT(B.*) FROM BUILD B" +
                "                LEFT JOIN (" +
                "                    SELECT R.BUILD,  R.VALIDATION_STAMP, VRS.STATUS " +
                "                    FROM VALIDATION_RUN R" +
                "                    INNER JOIN VALIDATION_RUN_STATUS VRS ON VRS.ID = (SELECT ID FROM VALIDATION_RUN_STATUS WHERE VALIDATION_RUN = R.ID ORDER BY ID DESC LIMIT 1)" +
                "                    AND R.RUN_ORDER = (SELECT MAX(RUN_ORDER) FROM VALIDATION_RUN WHERE BUILD = R.BUILD AND VALIDATION_STAMP = R.VALIDATION_STAMP)" +
                "                    ) S ON S.BUILD = B.ID" +
                "                WHERE B.BRANCH = :branch" +
                "                AND S.VALIDATION_STAMP = :validationStampId");
        // Status criteria
        if (statuses != null && !statuses.isEmpty()) {
            sql.append(format(" AND S.STATUS IN (%s)", getStatusesForSQLInClause(statuses)));
        }
        // Parameters
        MapSqlParameterSource params = params("branch", branch).addValue("validationStampId", validationStampId);
        // Limit & order
        sql.append(" ORDER BY B.ID DESC LIMIT 1");
        // OK
        return getFirstItem(
                sql.toString(),
                params,
                buildRowMapper
        );
    }

    @Override
    public TBuild findLastBuildWithPromotionLevel(int branch, String promotionLevel) {
        return getFirstItem(
                "SELECT B.* FROM BUILD B" +
                        " LEFT JOIN PROMOTED_RUN PR ON PR.BUILD = B.ID" +
                        " LEFT JOIN PROMOTION_LEVEL PL ON PL.ID = PR.PROMOTION_LEVEL" +
                        " WHERE B.BRANCH = :branch AND PL.NAME = :name" +
                        " ORDER BY B.ID DESC" +
                        " LIMIT 1",
                params("branch", branch)
                        .addValue("name", promotionLevel),
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
