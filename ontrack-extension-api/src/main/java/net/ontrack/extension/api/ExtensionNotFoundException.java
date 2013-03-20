package net.ontrack.extension.api;

import net.sf.jstring.support.CoreException;

public class ExtensionNotFoundException extends CoreException {

    public ExtensionNotFoundException(String name) {
        super(name);
    }
}
