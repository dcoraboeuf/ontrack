package net.ontrack.core.support;

public abstract class NotFoundException extends InputException {

    protected NotFoundException(Object... params) {
        super(params);
    }
}
