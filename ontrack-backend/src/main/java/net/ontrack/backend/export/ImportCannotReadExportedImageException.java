package net.ontrack.backend.export;

import net.sf.jstring.support.CoreException;

import java.io.IOException;

public class ImportCannotReadExportedImageException extends CoreException {
    public ImportCannotReadExportedImageException(String fieldName, IOException ex) {
        super(ex, fieldName, ex);
    }
}
