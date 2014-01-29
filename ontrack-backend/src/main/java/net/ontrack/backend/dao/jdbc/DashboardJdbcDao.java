package net.ontrack.backend.dao.jdbc;

import net.ontrack.backend.dao.DashboardDao;
import net.ontrack.backend.dao.model.TDashboard;
import net.ontrack.backend.db.SQL;
import net.ontrack.core.model.Ack;
import net.ontrack.dao.AbstractJdbcDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class DashboardJdbcDao extends AbstractJdbcDao implements DashboardDao {

    private final RowMapper<TDashboard> dashboardConfigRowMapper = new RowMapper<TDashboard>() {
        @Override
        public TDashboard mapRow(ResultSet rs, int rowNum) throws SQLException {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            List<Integer> branches = getDashboardCustomBranches(id);
            return new TDashboard(
                    id,
                    name,
                    branches
            );
        }
    };

    @Autowired
    public DashboardJdbcDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isValidationStampSelectedForBranch(int validationStampId, int branchId) {
        return getFirstItem(
                SQL.DASHBOARD_VALIDATION_STAMP_EXISTS,
                params("validationStamp", validationStampId).addValue("branch", branchId),
                Integer.class
        ) != null;
    }

    @Override
    @Transactional
    public Ack associateBranchValidationStamp(int branchId, int validationStampId) {
        if (!isValidationStampSelectedForBranch(validationStampId, branchId)) {
            return Ack.one(
                    getNamedParameterJdbcTemplate().update(
                            SQL.DASHBOARD_VALIDATION_STAMP_INSERT,
                            params("branch", branchId).addValue("validationStamp", validationStampId)
                    )
            );
        } else {
            return Ack.OK;
        }
    }

    @Override
    @Transactional
    public Ack dissociateBranchValidationStamp(int branchId, int validationStampId) {
        return Ack.one(
                getNamedParameterJdbcTemplate().update(
                        SQL.DASHBOARD_VALIDATION_STAMP_DELETE,
                        params("branch", branchId).addValue("validationStamp", validationStampId)
                )
        );
    }

    @Override
    public List<TDashboard> findAllCustoms() {
        return getJdbcTemplate().query(
                SQL.DASHBOARD_CUSTOM_ALL,
                dashboardConfigRowMapper
        );
    }

    private List<Integer> getDashboardCustomBranches(int id) {
        return getNamedParameterJdbcTemplate().queryForList(
                SQL.DASHBOARD_CUSTOM_BRANCHES,
                params("dashboard", id),
                Integer.class
        );
    }

    @Override
    public int createCustom(String name, List<Integer> branches) {
        int id = dbCreate(
                SQL.DASHBOARD_CUSTOM_CREATE,
                params("name", name)
        );
        for (int branch : branches) {
            getNamedParameterJdbcTemplate().update(
                    SQL.DASHBOARD_CUSTOM_BRANCH_INSERT,
                    params("dashboard", id).addValue("branch", branch)
            );
        }
        return id;
    }

    @Override
    public void updateCustom(int id, String name, List<Integer> branches) {
        getNamedParameterJdbcTemplate().update(
                SQL.DASHBOARD_CUSTOM_UPDATE,
                params("id", id).addValue("name", name)
        );
        getNamedParameterJdbcTemplate().update(
                SQL.DASHBOARD_CUSTOM_BRANCH_CLEANUP,
                params("dashboard", id)
        );
        for (int branch : branches) {
            getNamedParameterJdbcTemplate().update(
                    SQL.DASHBOARD_CUSTOM_BRANCH_INSERT,
                    params("dashboard", id).addValue("branch", branch)
            );
        }
    }

    @Override
    public TDashboard getCustom(int id) {
        return getNamedParameterJdbcTemplate().queryForObject(
                SQL.DASHBOARD_CUSTOM_BY_ID,
                params("id", id),
                dashboardConfigRowMapper
        );
    }

    @Override
    public Ack deleteCustom(int id) {
        return Ack.one(getNamedParameterJdbcTemplate().update(
                SQL.DASHBOARD_CUSTOM_DELETE,
                params("id", id)
        ));
    }
}
