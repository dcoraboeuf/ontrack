package net.ontrack.core.ui;

import net.ontrack.core.model.Account;
import net.ontrack.core.model.AccountCreationForm;
import net.ontrack.core.model.Ack;
import net.ontrack.core.model.ID;

import java.util.List;

public interface AdminUI {

    List<Account> accounts();

    Account account(int id);

    ID createAccount(AccountCreationForm form);

    Ack deleteAccount(int id);

}
