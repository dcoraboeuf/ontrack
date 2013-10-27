package net.ontrack.backend;

import net.ontrack.core.support.InputException;

public class ImportVersionException extends InputException {
    public ImportVersionException(String inputVersion, String targetVersion) {
        super(inputVersion, targetVersion);
    }
}
