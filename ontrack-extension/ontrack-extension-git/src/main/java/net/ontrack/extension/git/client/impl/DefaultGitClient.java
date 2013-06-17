package net.ontrack.extension.git.client.impl;

import net.ontrack.extension.git.client.GitClient;
import net.ontrack.extension.git.client.GitTag;
import net.ontrack.extension.git.model.GitConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class DefaultGitClient implements GitClient {

    private final Logger logger = LoggerFactory.getLogger(GitClient.class);
    private final GitRepository repository;
    private final GitConfiguration gitConfiguration;

    public DefaultGitClient(GitRepository repository, GitConfiguration gitConfiguration) {
        this.repository = repository;
        this.gitConfiguration = gitConfiguration;
    }

    @Override
    public Collection<GitTag> getTags() {
        repository.sync();
        // FIXME Implement net.ontrack.extension.git.client.impl.DefaultGitClient.getTags
        return null;
    }

    protected synchronized void sync() {

    }
}
