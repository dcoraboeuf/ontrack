package net.ontrack.backend.export;

import net.sf.jstring.support.CoreException;

public class ImportLinkedtEntityMissingException extends CoreException {
    public ImportLinkedtEntityMissingException(String container, int id) {
        super(container, id);
    }
}
