package net.ontrack.backend;

import net.sf.jstring.support.CoreException;

public class ConfigurationKeyMissingException extends CoreException {

    public ConfigurationKeyMissingException(ConfigurationKey key) {
        super(key.name());
    }

}
