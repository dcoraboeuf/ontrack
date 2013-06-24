package net.ontrack.extension.git.client;

import net.ontrack.extension.git.model.GitConfiguration;

public interface GitClientFactory {

    GitClient getClient(GitConfiguration gitConfiguration);

}
