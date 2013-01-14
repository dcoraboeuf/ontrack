package net.ontrack.core.security;

public interface SecurityUtils {

	boolean isLogged();

	User getCurrentUser();

	int getCurrentUserId();

	boolean isAdmin();

}
