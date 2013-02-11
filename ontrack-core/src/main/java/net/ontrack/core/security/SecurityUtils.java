package net.ontrack.core.security;

import net.ontrack.core.model.Account;

public interface SecurityUtils {

	boolean isLogged();

	Account getCurrentAccount();

	int getCurrentAccountId();

	boolean isAdmin();

}
