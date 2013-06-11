package net.ontrack.backend.dao.jdbc;

import net.ontrack.backend.dao.DashboardDao;
import net.ontrack.backend.db.SQL;
import net.ontrack.dao.AbstractJdbcDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

@Component
public class DashboardJdbcDao extends AbstractJdbcDao implements DashboardDao {

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
}
