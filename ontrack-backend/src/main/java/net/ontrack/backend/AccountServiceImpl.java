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
    public Account authenticate(String user, String password) {
        try {
            return getNamedParameterJdbcTemplate().queryForObject(
                    SQL.ACCOUNT_AUTHENTICATE,
                    params("user", user).addValue("password", StringUtils.upperCase(Sha512DigestUtils.shaHex(password))),
                    new RowMapper<Account>() {
                        @Override
                        public Account mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return new Account(rs.getInt("id"), rs.getString("name"), rs.getString("fullName"), rs.getString("roleName"));
                        }
                    }
            );
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }
}
