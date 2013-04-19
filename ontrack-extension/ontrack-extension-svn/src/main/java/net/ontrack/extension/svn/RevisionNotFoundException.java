package net.ontrack.extension.svn;

import net.ontrack.core.support.InputException;

public class RevisionNotFoundException extends InputException {
    public RevisionNotFoundException(long revision) {
        super(revision);
    }
}
