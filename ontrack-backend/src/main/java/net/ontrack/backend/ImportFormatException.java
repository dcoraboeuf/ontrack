package net.ontrack.backend;

import net.ontrack.core.support.InputException;

import java.io.IOException;

public class ImportFormatException extends InputException {
    public ImportFormatException(String name, IOException ex) {
        super(ex, name, ex);
    }
}
