package net.ontrack.backend;

import net.sf.jstring.support.CoreException;

public class ExportException extends CoreException {
    public ExportException(String uuid, Exception ex) {
        super(ex, uuid, ex);
    }
}
