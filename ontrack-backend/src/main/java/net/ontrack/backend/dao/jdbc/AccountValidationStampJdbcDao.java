package net.ontrack.backend.dao.jdbc;

import net.ontrack.backend.dao.AccountValidationStampDao;
import net.ontrack.backend.db.SQL;
import net.ontrack.dao.AbstractJdbcDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

@Component
public class AccountValidationStampJdbcDao extends AbstractJdbcDao implements AccountValidationStampDao {

    @Autowired
    public AccountValidationStampJdbcDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFiltered(int account, int validationStamp) {
        return getFirstItem(
                SQL.ACCOUNT_VALIDATION_STAMP_EXISTS,
                params("account", account).addValue("validationStamp", validationStamp),
                Integer.class) != null;
    }
}
