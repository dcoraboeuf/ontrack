package net.ontrack.backend.dao.jdbc;

import net.ontrack.backend.dao.GlobalAuthorizationDao;
import net.ontrack.backend.db.SQL;
import net.ontrack.core.model.Ack;
import net.ontrack.core.security.GlobalFunction;
import net.ontrack.dao.AbstractJdbcDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class GlobalAuthorizationJdbcDao extends AbstractJdbcDao implements GlobalAuthorizationDao {

    @Autowired
    public GlobalAuthorizationJdbcDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Ack set(int account, GlobalFunction fn) {
        // Deletes any previous ACL
        unset(account, fn);
        // Inserts this one
        return Ack.one(
                getNamedParameterJdbcTemplate().update(
                        SQL.GLOBAL_AUTHORIZATION_SET,
                        params("account", account).addValue("fn", fn.name())
                )
        );
    }

    @Override
    public Ack unset(int account, GlobalFunction fn) {
        return Ack.one(
                getNamedParameterJdbcTemplate().update(
                        SQL.GLOBAL_AUTHORIZATION_UNSET,
                        params("account", account).addValue("fn", fn.name())
                )
        );
    }
}
