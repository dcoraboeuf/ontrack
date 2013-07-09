package net.ontrack.backend.dao.jdbc;

import net.sf.jstring.support.CoreException;

import java.io.IOException;

public class FilterIOException extends CoreException {
    public FilterIOException(IOException e) {
        super(e);
    }
}
