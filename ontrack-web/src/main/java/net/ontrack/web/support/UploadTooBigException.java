package net.ontrack.web.support;

import net.ontrack.core.support.InputException;

public class UploadTooBigException extends InputException {
    public UploadTooBigException(long fileSizeMaxInK) {
        super(fileSizeMaxInK);
    }
}
