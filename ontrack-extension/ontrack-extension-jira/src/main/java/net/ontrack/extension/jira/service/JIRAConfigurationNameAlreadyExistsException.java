package net.ontrack.extension.jira.service;

import net.ontrack.core.support.InputException;

public class JIRAConfigurationNameAlreadyExistsException extends InputException {

    public JIRAConfigurationNameAlreadyExistsException(String name) {
        super(name);
    }
}
