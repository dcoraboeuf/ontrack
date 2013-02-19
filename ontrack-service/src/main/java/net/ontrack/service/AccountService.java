package net.ontrack.service;

import net.ontrack.core.model.Account;
import net.ontrack.core.model.AccountCreationForm;
import net.ontrack.core.model.Ack;

import java.util.List;

public interface AccountService {

    Account authenticate(String user, String password);

    String getRole(String mode, String user);

    Account getAccount(String mode, String user);

    List<Account> getAccounts();

    Ack createAccount(AccountCreationForm form);
}
