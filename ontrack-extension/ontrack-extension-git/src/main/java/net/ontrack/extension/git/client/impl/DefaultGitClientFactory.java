package net.ontrack.extension.git.client.impl;

import net.ontrack.extension.git.client.GitClient;
import net.ontrack.extension.git.client.GitClientFactory;
import net.ontrack.extension.git.model.GitConfiguration;
import org.springframework.stereotype.Component;

@Component
public class DefaultGitClientFactory implements GitClientFactory {

    @Override
    public GitClient getClient(GitConfiguration gitConfiguration) {
        return new DefaultGitClient(gitConfiguration);
    }

}
