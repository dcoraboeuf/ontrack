package net.ontrack.core.ui;

import net.ontrack.core.model.*;
import net.ontrack.core.security.GlobalFunction;

import java.util.List;

public interface AdminUI {

    List<GlobalFunction> getGlobalFunctions();

    List<Account> accounts();

    Account account(int id);

    ID createAccount(AccountCreationForm form);

    Ack deleteAccount(int id);

    Ack enableExtension(String name);

    Ack disableExtension(String name);

    List<AccountSummary> accountLookup(String query);

}
