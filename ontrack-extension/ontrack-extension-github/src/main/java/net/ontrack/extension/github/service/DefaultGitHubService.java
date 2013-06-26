package net.ontrack.extension.github.service;

import net.ontrack.core.model.Entity;
import net.ontrack.extension.api.property.PropertiesService;
import net.ontrack.extension.github.GitHubExtension;
import net.ontrack.extension.github.GitHubProjectProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultGitHubService implements GitHubService {

    private final PropertiesService propertiesService;

    @Autowired
    public DefaultGitHubService(PropertiesService propertiesService) {
        this.propertiesService = propertiesService;
    }

    @Override
    public String getGitHubProject(int projectId) {
        return propertiesService.getPropertyValue(
                Entity.PROJECT,
                projectId,
                GitHubExtension.EXTENSION,
                GitHubProjectProperty.NAME);
    }
}
