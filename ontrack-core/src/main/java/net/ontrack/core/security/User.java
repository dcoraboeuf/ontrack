package net.ontrack.core.security;

import org.springframework.security.core.userdetails.UserDetails;

public interface User extends UserDetails {
	
	boolean isAnonymous();
	
	int getId();
	
	String getEmail();

	boolean isAdmin();

}
