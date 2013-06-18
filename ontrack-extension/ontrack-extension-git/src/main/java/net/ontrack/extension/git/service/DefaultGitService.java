package net.ontrack.extension.git.service;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.ontrack.core.model.*;
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
import net.ontrack.extension.git.model.*;
import net.ontrack.service.ControlService;
import net.ontrack.service.ManagementService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DefaultGitService implements GitService {

    private final Logger logger = LoggerFactory.getLogger(GitService.class);
    private final SecurityUtils securityUtils;
    private final PropertiesService propertiesService;
    private final ManagementService managementService;
    private final ControlService controlService;
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
            ControlService controlService, GitClientFactory gitClientFactory) {
        this.securityUtils = securityUtils;
        this.propertiesService = propertiesService;
        this.managementService = managementService;
        this.controlService = controlService;
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
                        try {
                            doImportBuilds(branchId, form);
                        } catch (Exception ex) {
                            logger.error("[git] Cannot import builds", ex);
                        }
                        return null;
                    }
                });
            }
        });
    }

    @Override
    public ChangeLogSummary getChangeLogSummary(Locale locale, int branchId, int from, int to) {
        // Gets the branch
        BranchSummary branch = managementService.getBranch(branchId);
        // Gets the build information
        ChangeLogBuild buildFrom = getBuild(locale, from);
        ChangeLogBuild buildTo = getBuild(locale, to);
        // OK
        return new ChangeLogSummary(
                UUID.randomUUID().toString(),
                branch,
                buildFrom,
                buildTo
        );
    }

    @Override
    public ChangeLogCommits getChangeLogCommits(ChangeLogSummary summary) {
        // FIXME Implement net.ontrack.extension.git.service.DefaultGitService.getChangeLogCommits
        return null;
    }

    protected ChangeLogBuild getBuild(Locale locale, int buildId) {
        // Gets the build basic information
        BuildSummary build = managementService.getBuild(buildId);
        // OK
        return new ChangeLogBuild(
                build,
                managementService.getBuildValidationStamps(locale, build.getId()),
                managementService.getBuildPromotionLevels(locale, build.getId())
        );
    }

    protected void doImportBuilds(int branchId, GitImportBuildsForm form) {
        // Gets the branch Git configuration
        GitConfiguration gitConfiguration = getGitConfiguration(branchId);
        // Checks the configuration
        checkGitConfiguration(gitConfiguration);
        // Gets the Git client
        GitClient gitClient = gitClientFactory.getClient(gitConfiguration);
        // Gets the list of tags
        logger.debug("[git] Getting list of tags");
        Collection<GitTag> tags = gitClient.getTags();
        // Pattern for the tags
        final Pattern tagPattern = getTagRegex(form);
        // Creates the builds
        logger.debug("[git] Creating builds from tags");
        for (GitTag tag : tags) {
            String tagName = tag.getName();
            // Filters the tags according to the branch tag pattern
            Matcher matcher = tagPattern.matcher(tagName);
            if (matcher.matches()) {
                logger.info("[git] Creating build for tag {}", tagName);
                String buildName = matcher.group(1);
                logger.info("[git] Creating build {} from tag {}", buildName, tagName);
                controlService.createBuild(branchId, new BuildCreationForm(
                        buildName,
                        "Imported from Git tag " + tagName,
                        PropertiesCreationForm.create()
                ));
            }
        }
    }

    private Pattern getTagRegex(GitImportBuildsForm form) {
        final Pattern tagPattern;
        String tag = form.getTagPattern();
        if (StringUtils.isNotBlank(tag)) {
            tagPattern = Pattern.compile(tag);
        } else {
            tagPattern = Pattern.compile("(.*)");
        }
        return tagPattern;
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
