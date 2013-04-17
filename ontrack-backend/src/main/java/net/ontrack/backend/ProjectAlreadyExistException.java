package net.ontrack.backend;

import net.ontrack.core.support.InputException;

public class ProjectAlreadyExistException extends InputException {
    public ProjectAlreadyExistException(String name) {
        super(name);
    }
}
