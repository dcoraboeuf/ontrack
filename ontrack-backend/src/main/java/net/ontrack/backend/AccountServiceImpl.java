package net.ontrack.backend;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import net.ontrack.backend.dao.AccountDao;
import net.ontrack.backend.dao.CommentDao;
import net.ontrack.backend.dao.EventDao;
import net.ontrack.backend.dao.ValidationRunStatusDao;
import net.ontrack.backend.dao.model.TAccount;
import net.ontrack.core.model.*;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.core.validation.AccountValidation;
import net.ontrack.core.validation.Validations;
import net.ontrack.service.AccountService;
import net.ontrack.service.EventService;
import net.sf.jstring.Strings;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class AccountServiceImpl extends AbstractServiceImpl implements AccountService {

    private final Strings strings;
    private final AccountDao accountDao;
    private final CommentDao commentDao;
    private final ValidationRunStatusDao validationRunStatusDao;
    private final EventDao eventDao;
    private final Function<TAccount, Account> accountFunction = new Function<TAccount, Account>() {
        @Override
        public Account apply(TAccount t) {
            if (t == null) {
                return null;
            } else {
                return new Account(
                        t.getId(),
                        t.getName(),
                        t.getFullName(),
                        t.getEmail(),
                        t.getRoleName(),
                        t.getMode(),
                        strings.getSupportedLocales().filterForLookup(t.getLocale())
                );
            }
        }
    };

    @Autowired
    public AccountServiceImpl(ValidatorService validatorService, EventService eventService, Strings strings, AccountDao accountDao, CommentDao commentDao, ValidationRunStatusDao validationRunStatusDao, EventDao eventDao) {
        super(validatorService, eventService);
        this.strings = strings;
        this.accountDao = accountDao;
        this.commentDao = commentDao;
        this.validationRunStatusDao = validationRunStatusDao;
        this.eventDao = eventDao;
    }

    @Override
    @Transactional(readOnly = true)
    public Account authenticate(String user, String password) {
        return accountFunction.apply(accountDao.findByNameAndPassword(user, password));
    }

    @Override
    @Transactional(readOnly = true)
    public String getRole(String mode, String user) {
        return accountDao.getRoleByModeAndName(mode, user);
    }

    @Override
    @Transactional(readOnly = true)
    public Account getAccount(String mode, String user) {
        return accountFunction.apply(
                accountDao.findByModeAndName(mode, user)
        );
    }

    @Override
    @Transactional(readOnly = true)
    @Secured(SecurityRoles.ADMINISTRATOR)
    public Account getAccount(int id) {
        return accountFunction.apply(
                accountDao.getByID(id)
        );
    }

    @Override
    @Transactional(readOnly = true)
    @Secured(SecurityRoles.ADMINISTRATOR)
    public List<Account> getAccounts() {
        return Lists.transform(
                accountDao.findAll(),
                accountFunction
        );
    }

    @Override
    @Transactional
    @Secured(SecurityRoles.ADMINISTRATOR)
    public ID createAccount(final AccountCreationForm form) {
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
        // OK
        return accountDao.createAccount(
                form.getName(),
                form.getFullName(),
                form.getEmail(),
                form.getRoleName(),
                form.getMode(),
                form.getPassword()
        );
    }

    @Override
    @Transactional
    @Secured(SecurityRoles.ADMINISTRATOR)
    public void deleteAccount(int id) {
        accountDao.deleteAccount(id);
    }

    @Override
    @Transactional
    @Secured(SecurityRoles.ADMINISTRATOR)
    public void updateAccount(int id, AccountUpdateForm form) {
        // Gets the existing account
        Account account = getAccount(id);
        // Previous values
        String oldName = account.getName();
        String oldFullName = account.getFullName();
        // New values
        String name = form.getName();
        String fullName = form.getFullName();
        // Differences in names?
        boolean differentNames = !StringUtils.equals(oldName, name)
                || !StringUtils.equals(oldFullName, fullName);
        // Updates the account
        accountDao.updateAccount(
                id,
                name,
                fullName,
                form.getEmail(),
                form.getRoleName()
        );
        // Renaming?
        if (differentNames) {
            commentDao.renameAuthor(id, fullName);
            validationRunStatusDao.renameAuthor(id, fullName);
            eventDao.renameAuthor(id, fullName);
        }
    }

    @Override
    @Transactional
    @Secured(SecurityRoles.ADMINISTRATOR)
    public Ack changePassword(int id, PasswordChangeForm form) {
        // Gets the existing account
        Account account = getAccount(id);
        // Checks the mode
        if ("builtin".equals(account.getMode())) {
            // DAO
            return accountDao.changePassword(id, form.getOldPassword(), form.getNewPassword());
        } else {
            // Cannot change password in this case
            return Ack.NOK;
        }
    }

    @Override
    @Transactional
    @Secured(SecurityRoles.ADMINISTRATOR)
    public Ack changeEmail(int id, EmailChangeForm form) {
        // Gets the existing account
        Account account = getAccount(id);
        // Checks the mode
        if ("builtin".equals(account.getMode())) {
            // DAO
            return accountDao.changeEmail(id, form.getPassword(), form.getEmail());
        } else {
            // Cannot change password in this case
            return Ack.NOK;
        }
    }

    @Override
    @Transactional
    @Secured(SecurityRoles.ADMINISTRATOR)
    public Ack resetPassword(int id, String password) {
        // Gets the existing account
        Account account = getAccount(id);
        // Checks the mode
        if ("builtin".equals(account.getMode())) {
            // DAO
            return accountDao.resetPassword(id, password);
        } else {
            // Cannot change password in this case
            return Ack.NOK;
        }
    }
}
