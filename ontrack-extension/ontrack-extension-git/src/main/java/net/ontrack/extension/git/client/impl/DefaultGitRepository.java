package net.ontrack.extension.git.client.impl;

import java.io.File;

public class DefaultGitRepository implements GitRepository {

    private final File wd;
    private final String remote;
    private final String id;

    public DefaultGitRepository(File wd, String remote, String id) {
        this.wd = wd;
        this.remote = remote;
        this.id = id;
    }

    @Override
    public synchronized void sync() {
        // Clone or update
        if (isSetup()) {
            // FIXME Pull
        } else {
            // FIXME Clone
        }
    }

    @Override
    public synchronized boolean isSetup() {
        return new File(wd, ".git").exists();
    }
}
