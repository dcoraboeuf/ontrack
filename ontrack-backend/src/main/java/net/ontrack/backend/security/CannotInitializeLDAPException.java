package net.ontrack.backend.security;

import net.sf.jstring.support.CoreException;

public class CannotInitializeLDAPException extends CoreException {

    public CannotInitializeLDAPException(Exception e) {
        super(e, e);
    }
}
