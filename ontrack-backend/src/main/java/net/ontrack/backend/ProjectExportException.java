package net.ontrack.backend;

import net.sf.jstring.support.CoreException;

public class ProjectExportException extends CoreException {
    public ProjectExportException(String uuid, Exception ex) {
        super(ex, uuid, ex);
    }
}
