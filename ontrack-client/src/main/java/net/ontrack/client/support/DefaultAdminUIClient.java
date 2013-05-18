package net.ontrack.client.support;

import net.ontrack.client.AdminUIClient;
import net.ontrack.core.model.Account;
import net.ontrack.core.model.AccountCreationForm;
import net.ontrack.core.model.Ack;
import net.ontrack.core.model.ID;

import java.util.List;

import static java.lang.String.format;

public class DefaultAdminUIClient extends AbstractClient implements AdminUIClient {

    public DefaultAdminUIClient(String url) {
        super(url);
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
}
