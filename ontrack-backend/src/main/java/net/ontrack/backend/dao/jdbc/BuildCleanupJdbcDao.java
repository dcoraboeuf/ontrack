package net.ontrack.backend.dao.jdbc;

import net.ontrack.backend.dao.BuildCleanupDao;
import net.ontrack.backend.dao.model.TBuildCleanup;
import net.ontrack.backend.db.SQL;
import net.ontrack.core.model.Ack;
import net.ontrack.core.model.ID;
import net.ontrack.dao.AbstractJdbcDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@Component
public class BuildCleanupJdbcDao extends AbstractJdbcDao implements BuildCleanupDao {

    @Autowired
    public BuildCleanupJdbcDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    @Transactional
    public ID saveBuildCleanUp(int branch, int schedule, int retention, Set<Integer> excludedPromotionLevels) {
        // Is there any previous configuration
        TBuildCleanup conf = findBuildCleanUp(branch);
        if (conf != null) {
            // Deletes it first
            getNamedParameterJdbcTemplate().update(
                    SQL.BUILD_CLEANUP_DELETE,
                    params("branch", branch)
            );
        }
        // Inserts
        int id = dbCreate(
                SQL.BUILD_CLEANUP_INSERT,
                params("branch", branch).addValue("schedule", schedule).addValue("retention", retention)
        );
        // Excluded promotion levels
        if (excludedPromotionLevels != null) {
            for (int promotionLevel : excludedPromotionLevels) {
                getNamedParameterJdbcTemplate().update(
                        SQL.BUILD_CLEANUP_PROMOTION_INSERT,
                        params("buildCleanup", id).addValue("promotionLevel", promotionLevel)
                );
            }
        }
        // OK
        return ID.success(id);
    }

    @Override
    @Transactional
    public Ack removeBuildCleanUp(int branch) {
        return Ack.one(getNamedParameterJdbcTemplate().update(
                SQL.BUILD_CLEANUP_DELETE,
                params("branch", branch)
        ));
    }

    @Override
    public TBuildCleanup findBuildCleanUp(int branch) {
        return getFirstItem(
                SQL.BUILD_CLEANUP_FIND_BY_BRANCH,
                params("branch", branch),
                new RowMapper<TBuildCleanup>() {
                    @Override
                    public TBuildCleanup mapRow(ResultSet rs, int rowNum) throws SQLException {
                        int id = rs.getInt("id");
                        int schedule = rs.getInt("schedule");
                        int retention = rs.getInt("retention");
                        int branchId = rs.getInt("branch");
                        Set<Integer> excludedPromotionLevels = new HashSet<>();
                        excludedPromotionLevels.addAll(
                                getNamedParameterJdbcTemplate().queryForList(
                                        SQL.BUILD_CLEANUP_PROMOTION_BY_ID,
                                        params("id", id),
                                        Integer.class
                                )
                        );
                        return new TBuildCleanup(
                                id,
                                branchId,
                                schedule,
                                retention,
                                excludedPromotionLevels
                        );
                    }
                }
        );
    }
}
