package net.ontrack.core.ui;

import net.ontrack.core.model.*;
import net.ontrack.core.security.GlobalFunction;
import net.ontrack.core.security.ProjectAuthorization;
import net.ontrack.core.security.ProjectFunction;
import net.ontrack.core.security.ProjectRole;

import java.util.List;

public interface AdminUI {

    List<GlobalFunction> getGlobalFunctions();

    List<GlobalACLSummary> getGlobalACL();

    Ack setGlobalACL(int account, GlobalFunction fn);

    Ack unsetGlobalACL(int account, GlobalFunction fn);

    List<ProjectRole> getProjectRoles();

    List<ProjectFunction> getProjectFunctions();

    Ack setProjectACL(String project, int account, ProjectRole role);

    Ack unsetProjectACL(String project, int account);

    List<ProjectAuthorization> getProjectACLList(String project);

    List<Account> accounts();

    Account account(int id);

    ID createAccount(AccountCreationForm form);

    Ack deleteAccount(int id);

    Ack enableExtension(String name);

    Ack disableExtension(String name);

    List<AccountSummary> accountLookup(String query);

}
