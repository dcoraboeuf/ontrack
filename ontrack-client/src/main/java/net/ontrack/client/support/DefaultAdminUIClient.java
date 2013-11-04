package net.ontrack.client.support;

import net.ontrack.client.AdminUIClient;
import net.ontrack.core.model.*;
import net.ontrack.core.security.GlobalFunction;
import net.ontrack.core.security.ProjectAuthorization;
import net.ontrack.core.security.ProjectFunction;
import net.ontrack.core.security.ProjectRole;

import java.util.List;

import static java.lang.String.format;

public class DefaultAdminUIClient extends AbstractClient implements AdminUIClient {

    public DefaultAdminUIClient(String url) {
        super(url);
    }

    @Override
    public List<GlobalFunction> getGlobalFunctions() {
        return list(
                getDefaultLocale(),
                format("/ui/admin/acl/global/fn"),
                GlobalFunction.class
        );
    }

    @Override
    public List<GlobalACLSummary> getGlobalACL() {
        return list(
                getDefaultLocale(),
                format("/ui/admin/acl/global"),
                GlobalACLSummary.class
        );
    }

    @Override
    public Ack setGlobalACL(int account, GlobalFunction fn) {
        return put(
                getDefaultLocale(),
                format("/ui/admin/acl/global/%d/%s", account, fn),
                Ack.class,
                null
        );
    }

    @Override
    public Ack unsetGlobalACL(int account, GlobalFunction fn) {
        return delete(
                getDefaultLocale(),
                format("/ui/admin/acl/global/%d/%s", account, fn),
                Ack.class
        );
    }

    @Override
    public List<ProjectRole> getProjectRoles() {
        return list(
                getDefaultLocale(),
                format("/ui/admin/acl/project/role"),
                ProjectRole.class
        );
    }

    @Override
    public List<ProjectFunction> getProjectFunctions() {
        return list(
                getDefaultLocale(),
                format("/ui/admin/acl/project/fn"),
                ProjectFunction.class
        );
    }

    @Override
    public Ack setProjectACL(String project, int account, ProjectRole role) {
        return put(
                getDefaultLocale(),
                format("/ui/admin/acl/project/%s/%d/%s", project, account, role),
                Ack.class,
                null
        );
    }

    @Override
    public Ack unsetProjectACL(String project, int account) {
        return delete(
                getDefaultLocale(),
                format("/ui/admin/acl/project/%s/%d", project, account),
                Ack.class
        );
    }

    @Override
    public List<ProjectAuthorization> getProjectACLList(String project) {
        return list(
                getDefaultLocale(),
                format("/ui/admin/acl/project/%s", project),
                ProjectAuthorization.class
        );
    }

    @Override
    public List<Account> accounts() {
        return list(
                getDefaultLocale(),
                format("/ui/admin/accounts"),
                Account.class
        );
    }

    @Override
    public Account account(int id) {
        return get(
                getDefaultLocale(),
                format("/ui/admin/accounts/%d", id),
                Account.class
        );
    }

    @Override
    public ID createAccount(AccountCreationForm form) {
        return post(
                getDefaultLocale(),
                format("/ui/admin/accounts"),
                ID.class,
                form
        );
    }

    @Override
    public Ack deleteAccount(int id) {
        return delete(
                getDefaultLocale(),
                format("/ui/admin/accounts/%d", id),
                Ack.class
        );
    }

    @Override
    public Ack enableExtension(String name) {
        return put(
                getDefaultLocale(),
                format("/ui/admin/extensions/%s", name),
                Ack.class,
                null
        );
    }

    @Override
    public Ack disableExtension(String name) {
        return delete(
                getDefaultLocale(),
                format("/ui/admin/extensions/%s", name),
                Ack.class
        );
    }

    @Override
    public List<AccountSummary> accountLookup(String query) {
        return list(
                getDefaultLocale(),
                format("/ui/admin/account/lookup/%s", query),
                AccountSummary.class
        );
    }
}
