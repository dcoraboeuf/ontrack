package net.ontrack.backend.dao.jdbc;

import net.ontrack.backend.dao.GlobalAuthorizationDao;
import net.ontrack.backend.dao.model.TGlobalAuthorization;
import net.ontrack.backend.db.SQL;
import net.ontrack.core.model.Ack;
import net.ontrack.core.security.GlobalFunction;
import net.ontrack.dao.AbstractJdbcDao;
import net.ontrack.dao.SQLUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class GlobalAuthorizationJdbcDao extends AbstractJdbcDao implements GlobalAuthorizationDao {

    private final RowMapper<TGlobalAuthorization> globalAuthorizationRowMapper = new RowMapper<TGlobalAuthorization>() {
        @Override
        public TGlobalAuthorization mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new TGlobalAuthorization(
                    rs.getInt("account"),
                    SQLUtils.getEnum(GlobalFunction.class, rs, "fn")
            );
        }
    };

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

    @Override
    public List<TGlobalAuthorization> all() {
        return getJdbcTemplate().query(
                SQL.GLOBAL_AUTHORIZATION_LIST,
                globalAuthorizationRowMapper
        );
    }

    @Override
    public List<TGlobalAuthorization> findByAccount(int account) {
        return getNamedParameterJdbcTemplate().query(
                SQL.GLOBAL_AUTHORIZATION_BY_ACCOUNT,
                params("account", account),
                globalAuthorizationRowMapper
        );
    }
}
