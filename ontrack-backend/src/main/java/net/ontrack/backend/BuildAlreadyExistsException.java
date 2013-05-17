package net.ontrack.backend;

import net.ontrack.core.support.NotFoundException;

public class BuildAlreadyExistsException extends NotFoundException {
    public BuildAlreadyExistsException(String name) {
        super(name);
    }
}
