package net.ontrack.backend;

import net.ontrack.core.model.Account;

public interface AccountService {

    Account authenticate(String user, String password);

    String getRole(String mode, String user);
}
