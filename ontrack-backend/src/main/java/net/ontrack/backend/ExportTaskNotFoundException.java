package net.ontrack.backend;

import net.ontrack.core.support.NotFoundException;

public class ExportTaskNotFoundException extends NotFoundException {
    public ExportTaskNotFoundException(String uuid) {
        super(uuid);
    }
}
