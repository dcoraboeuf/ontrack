package net.ontrack.backend;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import net.ontrack.backend.dao.*;
import net.ontrack.backend.dao.model.TAccount;
import net.ontrack.backend.dao.model.TProjectAuthorization;
import net.ontrack.core.model.*;
import net.ontrack.core.security.*;
import net.ontrack.core.validation.AccountValidation;
import net.ontrack.core.validation.Validations;
import net.ontrack.service.AccountService;
import net.ontrack.service.EventService;
import net.sf.jstring.Strings;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Lists.transform;

@Service
public class AccountServiceImpl extends AbstractServiceImpl implements AccountService {

    private final Strings strings;
    private final AccountDao accountDao;
    private final CommentDao commentDao;
    private final ValidationRunStatusDao validationRunStatusDao;
    private final EventDao eventDao;
    private final ProjectAuthorizationDao projectAuthorizationDao;
    private final GlobalAuthorizationDao globalAuthorizationDao;
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
    private final Function<TAccount, Account> accountACLFunction = new Function<TAccount, Account>() {
        @Override
        public Account apply(TAccount t) {
            return getACL(accountFunction.apply(t));
        }
    };
    private final Function<TAccount, AccountSummary> accountSummaryFn = new Function<TAccount, AccountSummary>() {
        @Override
        public AccountSummary apply(TAccount t) {
            return new AccountSummary(t.getId(), t.getName(), t.getFullName());
        }
    };

    @Autowired
    public AccountServiceImpl(ValidatorService validatorService, EventService eventService, Strings strings, AccountDao accountDao, CommentDao commentDao, ValidationRunStatusDao validationRunStatusDao, EventDao eventDao, ProjectAuthorizationDao projectAuthorizationDao, GlobalAuthorizationDao globalAuthorizationDao) {
        super(validatorService, eventService);
        this.strings = strings;
        this.accountDao = accountDao;
        this.commentDao = commentDao;
        this.validationRunStatusDao = validationRunStatusDao;
        this.eventDao = eventDao;
        this.projectAuthorizationDao = projectAuthorizationDao;
        this.globalAuthorizationDao = globalAuthorizationDao;
    }

    @Override
    @Transactional(readOnly = true)
    public Account authenticate(String user, String password) {
        return accountACLFunction.apply(accountDao.findByNameAndPassword(user, password));
    }

    @Override
    @Transactional(readOnly = true)
    public String getRole(String mode, String user) {
        return accountDao.getRoleByModeAndName(mode, user);
    }

    @Override
    @Transactional(readOnly = true)
    public Account getAccount(String mode, String user) {
        return accountACLFunction.apply(
                accountDao.findByModeAndName(mode, user)
        );
    }

    @Override
    @Transactional(readOnly = true)
    @GlobalGrant(GlobalFunction.ACCOUNT_MANAGEMENT)
    public Account getAccount(int id) {
        return accountFunction.apply(
                accountDao.getByID(id)
        );
    }

    @Override
    @Transactional(readOnly = true)
    @GlobalGrant(GlobalFunction.ACCOUNT_MANAGEMENT)
    public List<Account> getAccounts() {
        return Lists.transform(
                accountDao.findAll(),
                accountFunction
        );
    }

    @Override
    @Transactional
    @GlobalGrant(GlobalFunction.ACCOUNT_MANAGEMENT)
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
    @GlobalGrant(GlobalFunction.ACCOUNT_MANAGEMENT)
    public void deleteAccount(int id) {
        accountDao.deleteAccount(id);
    }

    @Override
    @Transactional
    @GlobalGrant(GlobalFunction.ACCOUNT_MANAGEMENT)
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
    @GlobalGrant(GlobalFunction.ACCOUNT_MANAGEMENT)
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
    @GlobalGrant(GlobalFunction.ACCOUNT_MANAGEMENT)
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
    @GlobalGrant(GlobalFunction.ACCOUNT_MANAGEMENT)
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

    @Override
    @Transactional
    @GlobalGrant(GlobalFunction.ACCOUNT_MANAGEMENT)
    public Ack changeLanguage(int id, String lang) {
        return accountDao.changeLanguage(id, strings.getSupportedLocales().filterForLookup(new Locale(lang)));
    }

    @Override
    @Transactional
    @ProjectGrant(ProjectFunction.ACL)
    public Ack setProjectACL(@ProjectGrantId int project, int account, ProjectRole role) {
        return projectAuthorizationDao.set(project, account, role);
    }

    @Override
    @Transactional
    @ProjectGrant(ProjectFunction.ACL)
    public Ack unsetProjectACL(@ProjectGrantId int project, int account) {
        return projectAuthorizationDao.unset(project, account);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectAuthorization> getProjectACLList(int project) {
        return transform(
                projectAuthorizationDao.findByProject(project),
                new Function<TProjectAuthorization, ProjectAuthorization>() {
                    @Override
                    public ProjectAuthorization apply(TProjectAuthorization t) {
                        return new ProjectAuthorization(
                                t.getProject(),
                                accountSummaryFn.apply(accountDao.getByID(t.getAccount())),
                                t.getRole()
                        );
                    }
                }
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Account> findAccountsForProjectACL(final int project, final ProjectFunction fn) {
        // Filtering
        return filter(
                transform(
                        transform(
                                accountDao.findAll(),
                                accountFunction
                        ),
                        new Function<Account, Account>() {
                            @Override
                            public Account apply(Account o) {
                                return getACL(o);
                            }
                        }
                ),
                new Predicate<Account>() {
                    @Override
                    public boolean apply(Account account) {
                        return account.isGranted(fn, project);
                    }
                }
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountSummary> accountLookup(String query) {
        return transform(
                accountDao.findByQuery(query),
                accountSummaryFn
        );
    }

    @Override
    @Transactional
    @GlobalGrant(GlobalFunction.ACCOUNT_MANAGEMENT)
    public Ack setGlobalACL(int account, GlobalFunction fn) {
        return globalAuthorizationDao.set(account, fn);
    }

    protected Account getACL(Account account) {
        if (account != null) {
            // Global functions (all functions for the admin, none for the other roles)
            if (SecurityRoles.ADMINISTRATOR.equals(account.getRoleName())) {
                for (GlobalFunction fn : GlobalFunction.values()) {
                    account = account.withGlobalACL(fn);
                }
            }
            // Functions for all projects
            List<TProjectAuthorization> authList = projectAuthorizationDao.findByAccount(account.getId());
            for (TProjectAuthorization auth : authList) {
                for (ProjectFunction fn : ProjectFunction.values()) {
                    if (fn.isAllowedForProjectRole(auth.getRole())) {
                        account = account.withProjectACL(fn, auth.getProject());
                    }
                }
            }
        }
        // OK
        return account;
    }
}
