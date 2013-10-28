package net.ontrack.core.security;

import net.sf.jstring.support.CoreException;

public class ProjectGrantIdMissingException extends CoreException {

    public ProjectGrantIdMissingException(String name) {
        super(name);
    }
}
