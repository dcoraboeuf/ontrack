package net.ontrack.extension.svn.service;

import net.sf.jstring.support.CoreException;
import org.tmatesoft.svn.core.SVNException;

public class SubversionException extends CoreException {
    public SubversionException(SVNException e) {
        super(e, e);

    }
}
