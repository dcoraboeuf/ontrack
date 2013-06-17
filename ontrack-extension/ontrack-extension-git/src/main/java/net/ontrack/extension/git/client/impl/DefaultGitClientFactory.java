package net.ontrack.extension.git.client.impl;

import net.ontrack.extension.git.client.GitClient;
import net.ontrack.extension.git.client.GitClientFactory;
import net.ontrack.extension.git.model.GitConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultGitClientFactory implements GitClientFactory {

    private final GitRepositoryManager repositoryManager;

    @Autowired
    public DefaultGitClientFactory(GitRepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    @Override
    public GitClient getClient(GitConfiguration gitConfiguration) {
        // Repository
        GitRepository repository = repositoryManager.getRepository(gitConfiguration.getRemote());
        // Client
        return new DefaultGitClient(repository, gitConfiguration);
    }

}
