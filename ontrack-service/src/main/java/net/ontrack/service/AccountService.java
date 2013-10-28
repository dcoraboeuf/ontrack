package net.ontrack.service;

import net.ontrack.core.model.*;
import net.ontrack.core.security.ProjectAuthorization;
import net.ontrack.core.security.ProjectFunction;
import net.ontrack.core.security.ProjectRole;

import java.util.Collection;
import java.util.List;

public interface AccountService {

    Account authenticate(String user, String password);

    String getRole(String mode, String user);

    Account getAccount(String mode, String user);

    Account getAccount(int id);

    List<Account> getAccounts();

    ID createAccount(AccountCreationForm form);

    void deleteAccount(int id);

    void updateAccount(int id, AccountUpdateForm form);

    Ack changePassword(int id, PasswordChangeForm form);

    Ack changeEmail(int accountId, EmailChangeForm form);

    Ack resetPassword(int id, String password);

    Ack changeLanguage(int id, String lang);

    Ack setProjectACL(int project, int account, ProjectRole role);

    Ack unsetProjectACL(int project, int account);

    List<ProjectAuthorization> getProjectACLList(int project);

    Collection<Account> findAccountsForProjectACL(int project, ProjectFunction fn);
}
