package net.ontrack.extension.github.client;

public interface GitHubClientConfiguratorFactory {

    GitHubClientConfigurator getGitHubConfigurator(int projectId);

}
