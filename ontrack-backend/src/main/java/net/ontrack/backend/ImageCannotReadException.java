package net.ontrack.backend;

import net.ontrack.core.support.InputException;

import java.io.IOException;

public class ImageCannotReadException extends InputException {
    public ImageCannotReadException(IOException e) {
        super(e);
    }
}
