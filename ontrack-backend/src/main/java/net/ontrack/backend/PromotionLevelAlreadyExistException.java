package net.ontrack.backend;

import net.ontrack.core.support.InputException;

public class PromotionLevelAlreadyExistException extends InputException {
    public PromotionLevelAlreadyExistException(String name) {
        super(name);
    }
}
