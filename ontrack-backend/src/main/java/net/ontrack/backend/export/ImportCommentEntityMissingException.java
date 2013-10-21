package net.ontrack.backend.export;

import net.sf.jstring.support.CoreException;

public class ImportCommentEntityMissingException extends CoreException {
    public ImportCommentEntityMissingException(int id) {
        super(id);
    }
}
