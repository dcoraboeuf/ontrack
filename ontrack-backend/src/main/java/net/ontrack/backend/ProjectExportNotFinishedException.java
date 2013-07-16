package net.ontrack.backend;

import net.sf.jstring.support.CoreException;

public class ProjectExportNotFinishedException extends CoreException {
    public ProjectExportNotFinishedException(String uuid) {
        super(uuid);
    }
}
