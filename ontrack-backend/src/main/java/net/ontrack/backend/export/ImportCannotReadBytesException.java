package net.ontrack.backend.export;

import net.sf.jstring.support.CoreException;

import java.io.IOException;

public class ImportCannotReadBytesException extends CoreException {
    public ImportCannotReadBytesException(String path, IOException ex) {
        super(ex, path, ex);
    }
}
