package net.ontrack.client.support;

import net.ontrack.client.AdminUIClient;
import net.ontrack.core.model.AccountCreationForm;
import net.ontrack.core.model.Ack;

import static java.lang.String.format;

public class DefaultAdminUIClient extends AbstractClient implements AdminUIClient {

    public DefaultAdminUIClient(String url) {
        super(url);
    }

    @Override
    public Ack createAccount(AccountCreationForm form) {
        return post(
                getDefaultLocale(),
                format("/ui/admin/accounts"),
                Ack.class,
                form
        );
    }
}
