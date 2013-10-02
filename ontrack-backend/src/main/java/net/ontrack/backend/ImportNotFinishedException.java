package net.ontrack.backend;

import net.sf.jstring.support.CoreException;

public class ImportNotFinishedException extends CoreException {
    public ImportNotFinishedException(String uuid) {
        super(uuid);
    }
}
