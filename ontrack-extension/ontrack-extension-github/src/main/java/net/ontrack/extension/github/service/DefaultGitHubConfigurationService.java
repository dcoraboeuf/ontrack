package net.ontrack.extension.github.service;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import net.ontrack.core.model.BranchSummary;
import net.ontrack.core.model.Entity;
import net.ontrack.core.model.ProjectSummary;
import net.ontrack.extension.api.property.PropertiesService;
import net.ontrack.extension.git.client.GitCommit;
import net.ontrack.extension.git.model.GitCommitInfo;
import net.ontrack.extension.git.service.GitService;
import net.ontrack.extension.github.GitHubExtension;
import net.ontrack.extension.github.GitHubProjectProperty;
import net.ontrack.extension.github.client.GitHubClientConfigurator;
import net.ontrack.extension.github.client.GitHubClientConfiguratorFactory;
import net.ontrack.extension.github.client.OntrackGitHubClient;
import net.ontrack.extension.github.model.GitHubCommit;
import net.ontrack.extension.github.model.GitHubIssue;
import net.ontrack.extension.github.model.GitHubIssueInfo;
import net.ontrack.service.ManagementService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class DefaultGitHubConfigurationService implements GitHubConfigurationService {

    private final PropertiesService propertiesService;

    @Autowired
    public DefaultGitHubConfigurationService(PropertiesService propertiesService) {
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
