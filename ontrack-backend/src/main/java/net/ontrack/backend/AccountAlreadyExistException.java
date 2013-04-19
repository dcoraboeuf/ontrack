package net.ontrack.backend;

import net.ontrack.core.support.InputException;

public class AccountAlreadyExistException extends InputException {
    public AccountAlreadyExistException(String name) {
        super(name);
    }
}
