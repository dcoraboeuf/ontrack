package net.ontrack.backend.security;

import net.sf.jstring.support.CoreException;

public class AsAdminCallException extends CoreException {
    public AsAdminCallException(Exception e) {
        super(e);
    }
}
