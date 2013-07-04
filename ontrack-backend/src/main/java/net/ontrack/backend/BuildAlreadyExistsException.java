package net.ontrack.backend;

import net.ontrack.core.support.InputException;

public class BuildAlreadyExistsException extends InputException {
    public BuildAlreadyExistsException(String name) {
        super(name);
    }
}
