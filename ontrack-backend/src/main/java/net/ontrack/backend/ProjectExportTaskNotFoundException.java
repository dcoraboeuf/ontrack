package net.ontrack.backend;

import net.ontrack.core.support.NotFoundException;

public class ProjectExportTaskNotFoundException extends NotFoundException {
    public ProjectExportTaskNotFoundException(String uuid) {
        super(uuid);
    }
}
