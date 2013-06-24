package net.ontrack.extension.git.client.impl;

import net.sf.jstring.support.CoreException;
import org.eclipse.jgit.api.errors.GitAPIException;

public class GitException extends CoreException {
    public GitException(GitAPIException e) {
        super(e);
    }
}
