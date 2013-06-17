package net.ontrack.extension.git.client.impl;

import net.sf.jstring.support.CoreException;

import java.util.concurrent.ExecutionException;

public class GitRepositoryManagerException extends CoreException {
    public GitRepositoryManagerException(String remote, ExecutionException e) {
        super(e, remote);
    }
}
