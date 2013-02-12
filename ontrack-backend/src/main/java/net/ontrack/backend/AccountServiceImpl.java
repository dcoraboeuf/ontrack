package net.ontrack.backend;

import net.ontrack.backend.db.SQL;
import net.ontrack.core.model.Account;
import net.ontrack.service.EventService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.token.Sha512DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import javax.validation.Validator;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class AccountServiceImpl extends AbstractServiceImpl implements AccountService {

    @Autowired
    public AccountServiceImpl(DataSource dataSource, Validator validator, EventService eventService) {
        super(dataSource, validator, eventService);
    }

    @Override
    @Transactional(readOnly = true)
    public Account authenticate(String user, String password) {
        try {
            return getNamedParameterJdbcTemplate().queryForObject(
                    SQL.ACCOUNT_AUTHENTICATE,
                    params("user", user).addValue("password", StringUtils.upperCase(Sha512DigestUtils.shaHex(password))),
                    new AccountRowMapper()
            );
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String getRole(String mode, String user) {
        return getFirstItem(SQL.ACCOUNT_ROLE, params("mode", mode).addValue("user", user), String.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Account getAccount(String mode, String user) {
        try {
            return getNamedParameterJdbcTemplate().queryForObject(
                    SQL.ACCOUNT,
                    params("user", user).addValue("mode", mode),
                    new AccountRowMapper()
            );
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    private static class AccountRowMapper implements RowMapper<Account> {
        @Override
        public Account mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Account(rs.getInt("id"), rs.getString("name"), rs.getString("fullName"), rs.getString("roleName"), rs.getString("mode"));
        }
    }
}
