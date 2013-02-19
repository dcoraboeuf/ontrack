package net.ontrack.backend;

import com.google.common.base.Predicate;
import net.ontrack.backend.db.SQL;
import net.ontrack.core.model.Account;
import net.ontrack.core.model.AccountCreationForm;
import net.ontrack.core.model.Ack;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.core.support.MapBuilder;
import net.ontrack.core.validation.AccountValidation;
import net.ontrack.core.validation.Validations;
import net.ontrack.service.AccountService;
import net.ontrack.service.EventService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.token.Sha512DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import javax.validation.Validator;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

@Service
public class AccountServiceImpl extends AbstractServiceImpl implements AccountService {

    private final RowMapper<Account> accountRowMapper = new RowMapper<Account>() {
        @Override
        public Account mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Account(rs.getInt("id"), rs.getString("name"), rs.getString("fullName"), rs.getString("roleName"), rs.getString("mode"));
        }
    };

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
                    accountRowMapper
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
                    accountRowMapper
            );
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Secured(SecurityRoles.ADMINISTRATOR)
    public List<Account> getAccounts() {
        return getJdbcTemplate().query(
                SQL.ACCOUNT_LIST,
                accountRowMapper
        );
    }

    @Override
    @Transactional
    @Secured(SecurityRoles.ADMINISTRATOR)
    public Ack createAccount(final AccountCreationForm form) {
        // Validation
        validate(form, AccountValidation.class);
        // Validation: role
        List<String> roles = Arrays.asList(SecurityRoles.ADMINISTRATOR, SecurityRoles.CONTROLLER, SecurityRoles.USER);
        validate(form.getRoleName(),
                Validations.oneOf(roles),
                "net.ontrack.core.model.Account.roleName.incorrect",
                StringUtils.join(roles, ","));
        // Validation: mode
        // TODO Gets the list of modes from the registered services
        List<String> modes = Arrays.asList("builtin", "ldap");
        validate(form.getMode(),
                Validations.oneOf(modes),
                "net.ontrack.core.model.Account.mode.incorrect",
                StringUtils.join(modes, ","));
        // Validation: checks the password
        validate(form.getPassword(), new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                return !"builtin".equals(form.getMode()) || StringUtils.isNotBlank(input);
            }
        }, "net.ontrack.core.model.Account.password.requiredForBuiltin");
        // Creation
        int count = dbCreate(
                SQL.ACCOUNT_CREATE,
                MapBuilder.params("name", form.getName())
                        .with("fullName", form.getFullName())
                        .with("roleName", form.getRoleName())
                        .with("mode", form.getMode())
                        .with("password", form.getPassword())
                        .get()
        );
        // OK
        return Ack.one(count);
    }
}
