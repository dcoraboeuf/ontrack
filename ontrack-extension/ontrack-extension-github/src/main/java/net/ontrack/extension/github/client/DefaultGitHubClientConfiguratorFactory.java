package net.ontrack.extension.github.client;

import net.ontrack.core.model.Entity;
import net.ontrack.extension.api.property.PropertiesService;
import net.ontrack.extension.github.GitHubAuthenticationProperty;
import net.ontrack.extension.github.GitHubExtension;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultGitHubClientConfiguratorFactory implements GitHubClientConfiguratorFactory {

    private final PropertiesService propertiesService;

    @Autowired
    public DefaultGitHubClientConfiguratorFactory(PropertiesService propertiesService) {
        this.propertiesService = propertiesService;
    }

    @Override
    public GitHubClientConfigurator getGitHubConfigurator(int projectId) {
        // Gets the authentication parameters
        final String authentication = propertiesService.getPropertyValue(Entity.PROJECT, projectId, GitHubExtension.EXTENSION, GitHubAuthenticationProperty.NAME);
        // Configurator
        return new GitHubClientConfigurator() {
            @Override
            public void configure(GitHubClient client) {
                if (StringUtils.isNotBlank(authentication)) {
                    client.setOAuth2Token(authentication);
                }
            }
        };
    }
}
