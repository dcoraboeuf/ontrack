package net.ontrack.extension.git.client.impl;

import net.sf.jstring.support.CoreException;

import java.io.IOException;

public class GitIOException extends CoreException {
    public GitIOException(IOException e) {
        super(e);
    }
}
