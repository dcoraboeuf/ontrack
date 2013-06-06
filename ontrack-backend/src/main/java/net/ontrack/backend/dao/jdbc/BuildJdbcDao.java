package net.ontrack.backend.dao.jdbc;

import net.ontrack.backend.BuildAlreadyExistsException;
import net.ontrack.backend.Caches;
import net.ontrack.backend.dao.BuildDao;
import net.ontrack.backend.dao.PromotionLevelDao;
import net.ontrack.backend.dao.ValidationStampDao;
import net.ontrack.backend.dao.model.TBuild;
import net.ontrack.backend.dao.model.TValidationStamp;
import net.ontrack.backend.db.SQL;
import net.ontrack.core.model.*;
import net.ontrack.dao.AbstractJdbcDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
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
    private final PromotionLevelDao promotionLevelDao;

    @Autowired
    public BuildJdbcDao(DataSource dataSource, ValidationStampDao validationStampDao, PromotionLevelDao promotionLevelDao) {
        super(dataSource);
        this.validationStampDao = validationStampDao;
        this.promotionLevelDao = promotionLevelDao;
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
    public Integer findBuildAfterUsingNumericForm(int branchId, String buildName) {
        return getFirstItem(
                SQL.BUILD_BY_BRANCH_AND_NUMERIC_NAME,
                params("branch", branchId).addValue("name", buildName),
                Integer.class
        );
    }

    @Override
    @Transactional
    public Ack delete(int buildId) {
        return Ack.one(
                getNamedParameterJdbcTemplate().update(
                        SQL.BUILD_DELETE,
                        params("id", buildId)
                )
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
                "                LEFT JOIN PROPERTIES PP ON PP.BUILD = B.ID" +
                "                WHERE B.BRANCH = :branch");
        MapSqlParameterSource params = new MapSqlParameterSource("branch", branch);
        Integer sinceBuildId = null;
        // Since last promotion level
        String sincePromotionLevel = filter.getSincePromotionLevel();
        if (StringUtils.isNotBlank(sincePromotionLevel)) {
            // Gets the promotion level ID
            int promotionLevelId = promotionLevelDao.getByBranchAndName(branch, sincePromotionLevel).getId();
            // Gets the last build having this promotion level
            TBuild build = findLastBuildWithPromotionLevel(promotionLevelId);
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
                // Gets the validation stamp ID
                int validationStampId = validationStampDao.getByBranchAndName(branch, sinceValidationStamp.getValidationStamp()).getId();
                // Gets the last build having this validation stamp and the status
                TBuild build = findLastBuildWithValidationStamp(validationStampId, sinceValidationStamp.getStatuses());
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
        // Properties
        PropertyValue withProperty = filter.getWithProperty();
        if (withProperty != null) {
            sql.append(" AND PP.EXTENSION = :propertyExtension AND PP.NAME = :propertyName");
            params.addValue("propertyExtension", withProperty.getExtension());
            params.addValue("propertyName", withProperty.getName());
            String withPropertyValue = withProperty.getValue();
            if (StringUtils.isNotBlank(withPropertyValue)) {
                sql.append(" AND PP.VALUE REGEXP :propertyValue");
                params.addValue("propertyValue", withPropertyValue);
            }
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
    public TBuild findLastBuildWithValidationStamp(int validationStamp, Set<Status> statuses) {
        StringBuilder sql = new StringBuilder(
                "SELECT VR.BUILD FROM VALIDATION_RUN_STATUS VRS\n" +
                        "INNER JOIN VALIDATION_RUN VR ON VR.ID = VRS.VALIDATION_RUN\n" +
                        "WHERE VR.VALIDATION_STAMP = :validationStamp\n");
        // Status criteria
        if (statuses != null && !statuses.isEmpty()) {
            sql.append(format("AND VRS.STATUS IN (%s)\n", getStatusesForSQLInClause(statuses)));
        }
        // Order & limit
        sql.append("ORDER BY VR.BUILD DESC LIMIT 1\n");
        // Parameters
        MapSqlParameterSource params = params("validationStamp", validationStamp);
        // Build ID
        Integer buildId = getFirstItem(
                sql.toString(),
                params,
                Integer.class
        );
        // OK
        if (buildId != null) {
            return getById(buildId);
        } else {
            return null;
        }
    }

    @Override
    public TBuild findLastBuildWithPromotionLevel(int promotionLevel) {
        Integer buildId = getFirstItem(
                SQL.BUILD_LAST_FOR_PROMOTION_LEVEL,
                params("promotionLevel", promotionLevel),
                Integer.class
        );
        if (buildId != null) {
            return getById(buildId);
        } else {
            return null;
        }
    }

    @Override
    @Transactional
    public int createBuild(int branch, String name, String description) {
        try {
            return dbCreate(
                    SQL.BUILD_CREATE,
                    params("branch", branch)
                            .addValue("name", name)
                            .addValue("description", description));
        } catch (DuplicateKeyException ex) {
            throw new BuildAlreadyExistsException(name);
        }
    }
}
