package net.ontrack.backend;

import net.sf.jstring.support.CoreException;

public class ExportNotFinishedException extends CoreException {
    public ExportNotFinishedException(String uuid) {
        super(uuid);
    }
}
