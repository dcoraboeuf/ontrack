package net.ontrack.extension.api.property;

import net.ontrack.core.support.InputException;

public class InvalidURLException extends InputException {
    public InvalidURLException(String value) {
        super(value);
    }
}
