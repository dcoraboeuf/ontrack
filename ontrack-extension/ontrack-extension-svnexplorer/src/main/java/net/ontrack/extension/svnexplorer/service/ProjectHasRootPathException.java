package net.ontrack.extension.svnexplorer.service;

import net.sf.jstring.support.CoreException;

public class ProjectHasRootPathException extends CoreException {
    public ProjectHasRootPathException(String name) {
        super(name);
    }
}
