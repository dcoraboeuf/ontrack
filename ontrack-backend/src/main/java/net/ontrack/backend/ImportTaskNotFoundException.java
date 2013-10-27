package net.ontrack.backend;

import net.ontrack.core.support.NotFoundException;

public class ImportTaskNotFoundException extends NotFoundException {
    public ImportTaskNotFoundException(String uuid) {
        super(uuid);
    }
}
