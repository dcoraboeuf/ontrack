package net.ontrack.extension.git.client.impl;

import net.ontrack.extension.git.client.GitClient;
import net.ontrack.extension.git.client.GitTag;
import net.ontrack.extension.git.model.GitConfiguration;

import java.util.Collection;

public class DefaultGitClient implements GitClient {

    private final GitConfiguration gitConfiguration;

    public DefaultGitClient(GitConfiguration gitConfiguration) {
        this.gitConfiguration = gitConfiguration;
    }

    @Override
    public Collection<GitTag> getTags() {
        // FIXME Implement net.ontrack.extension.git.client.impl.DefaultGitClient.getTags
        return null;
    }
}
