package net.ontrack.extension.git.service;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.ontrack.core.model.BranchSummary;
import net.ontrack.core.model.Entity;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.extension.api.property.PropertiesService;
import net.ontrack.extension.git.GitBranchProperty;
import net.ontrack.extension.git.GitExtension;
import net.ontrack.extension.git.GitRemoteProperty;
import net.ontrack.extension.git.GitTagProperty;
import net.ontrack.extension.git.client.GitClient;
import net.ontrack.extension.git.client.GitClientFactory;
import net.ontrack.extension.git.client.GitTag;
import net.ontrack.extension.git.model.GitConfiguration;
import net.ontrack.extension.git.model.GitImportBuildsForm;
import net.ontrack.service.ManagementService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class DefaultGitService implements GitService {

    private final SecurityUtils securityUtils;
    private final PropertiesService propertiesService;
    private final ManagementService managementService;
    private final GitClientFactory gitClientFactory;
    // Threads for the import
    private final ExecutorService executorImportBuilds = Executors.newFixedThreadPool(
            1,
            new ThreadFactoryBuilder()
                    .setDaemon(true)
                    .setNameFormat("git-import-builds-%s")
                    .build());

    @Autowired
    public DefaultGitService(
            SecurityUtils securityUtils,
            PropertiesService propertiesService,
            ManagementService managementService,
            GitClientFactory gitClientFactory) {
        this.securityUtils = securityUtils;
        this.propertiesService = propertiesService;
        this.managementService = managementService;
        this.gitClientFactory = gitClientFactory;
    }

    @Override
    @Secured(SecurityRoles.ADMINISTRATOR)
    public void importBuilds(final int branchId, final GitImportBuildsForm form) {
        executorImportBuilds.submit(new Runnable() {
            @Override
            public void run() {
                securityUtils.asAdmin(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        doImportBuilds(branchId, form);
                        return null;
                    }
                });
            }
        });
    }

    protected void doImportBuilds(int branchId, GitImportBuildsForm form) {
        // Gets the branch Git configuration
        GitConfiguration gitConfiguration = getGitConfiguration(branchId);
        // Checks the configuration
        checkGitConfiguration(gitConfiguration);
        // Gets the Git client
        GitClient gitClient = gitClientFactory.getClient(gitConfiguration);
        // Gets the list of tags
        Collection<GitTag> tags = gitClient.getTags();
        // TODO Filters the tags according to the branch tag pattern
        // TODO Creates the builds
    }

    private void checkGitConfiguration(GitConfiguration gitConfiguration) {
        if (StringUtils.isBlank(gitConfiguration.getRemote())) {
            throw new GitProjectRemoteNotConfiguredException();
        }
    }

    private GitConfiguration getGitConfiguration(int branchId) {
        // Gets the branch
        BranchSummary branch = managementService.getBranch(branchId);
        // Project Id
        int projectId = branch.getProject().getId();
        // Properties
        return new GitConfiguration(
                propertiesService.getPropertyValue(Entity.PROJECT, projectId, GitExtension.EXTENSION, GitRemoteProperty.NAME),
                propertiesService.getPropertyValue(Entity.BRANCH, branchId, GitExtension.EXTENSION, GitBranchProperty.NAME),
                propertiesService.getPropertyValue(Entity.BRANCH, branchId, GitExtension.EXTENSION, GitTagProperty.NAME)
        );
    }
}
