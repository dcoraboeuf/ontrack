package net.ontrack.service.support;

import net.sf.jstring.support.CoreException;

import java.io.File;
import java.io.IOException;

public class CannotCreateWorkingDirException extends CoreException {

    public CannotCreateWorkingDirException(File wd, IOException e) {
        super(e, wd);
    }

}
