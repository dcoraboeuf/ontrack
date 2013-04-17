package net.ontrack.backend;

import net.ontrack.core.support.InputException;

public class BranchAlreadyExistException extends InputException {
    public BranchAlreadyExistException(String name) {
        super(name);
    }
}
