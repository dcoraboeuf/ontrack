package net.ontrack.extension.git.client.impl;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class DefaultGitRepository implements GitRepository {

    private final Logger logger = LoggerFactory.getLogger(GitRepository.class);

    private final File wd;
    private final String remote;
    private final String branch;
    private final String id;

    private Git git;

    public DefaultGitRepository(File wd, String remote, String branch, String id) {
        this.wd = wd;
        this.remote = remote;
        this.branch = branch;
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
    public String getBranch() {
        return branch;
    }

    @Override
    public synchronized GitRepository sync() throws GitAPIException {
        // Clone or update
        if (git == null) {
            logger.debug("[git] Cloning {} into {}", remote, wd);
            git = cloneRemote();
            logger.debug("[git] Clone done for {}", remote);
        }
        // TODO Pull
        else {
        }
        // OK
        return this;
    }

    @Override
    public Git git() {
        if (git == null) {
            throw new GitNotSyncException();
        }
        return git;
    }

    protected synchronized Git cloneRemote() throws GitAPIException {
        return new CloneCommand()
                .setBranch(branch)
                .setDirectory(wd)
                .setURI(remote)
                .call();
    }

}
