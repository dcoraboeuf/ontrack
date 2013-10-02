package net.ontrack.backend;

import net.sf.jstring.support.CoreException;

public class ImportException extends CoreException {
    public ImportException(String uuid, Exception ex) {
        super(ex, uuid, ex);
    }
}
