package net.ontrack.extension.git.client.impl;

import net.sf.jstring.support.CoreException;

import java.io.File;

public class GitCannotCloneException extends CoreException {
    public GitCannotCloneException(File wd) {
        super(wd.getAbsolutePath());
    }
}
