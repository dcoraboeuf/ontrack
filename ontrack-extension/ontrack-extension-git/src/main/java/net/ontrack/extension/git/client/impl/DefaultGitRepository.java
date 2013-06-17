package net.ontrack.extension.git.client.impl;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;

public class DefaultGitRepository implements GitRepository {

    private final File wd;
    private final String remote;
    private final String id;

    private Git git;

    public DefaultGitRepository(File wd, String remote, String id) {
        this.wd = wd;
        this.remote = remote;
        this.id = id;
    }

    @Override
    public File wd() {
        return wd;
    }

    @Override
    public String getRemote() {
        return remote;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public synchronized GitRepository sync() {
        // Clone or update
        if (git == null) {
            git = cloneRemote();
        }
        // TODO Pull
        else {
        }
        // OK
        return this;
    }

    protected synchronized Git cloneRemote() {
        try {
            return new CloneCommand()
                    .setBranch("master")
                    .setDirectory(wd)
                    .setURI(remote)
                    .call();
        } catch (GitAPIException e) {
            throw translationException(e);
        }
    }

    protected GitException translationException(GitAPIException e) {
        throw new GitException(e);
    }

}
