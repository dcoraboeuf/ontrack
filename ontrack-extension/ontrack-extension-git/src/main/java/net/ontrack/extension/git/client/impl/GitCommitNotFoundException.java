package net.ontrack.extension.git.client.impl;

import net.sf.jstring.support.CoreException;

public class GitCommitNotFoundException extends CoreException {
    public GitCommitNotFoundException(String name) {
        super(name);
    }
}
