package net.ontrack.backend;

import net.ontrack.core.support.InputException;

public class ValidationStampAlreadyExistException extends InputException {
    public ValidationStampAlreadyExistException(String name) {
        super(name);
    }
}
