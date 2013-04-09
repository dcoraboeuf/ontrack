package net.ontrack.extension.svnexplorer.ui;

import net.ontrack.core.support.InputException;

public class ChangeLogUUIDException extends InputException {
    public ChangeLogUUIDException(String uuid) {
        super(uuid);
    }
}
