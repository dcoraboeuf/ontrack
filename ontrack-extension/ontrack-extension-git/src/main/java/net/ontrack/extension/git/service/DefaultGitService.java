package net.ontrack.extension.git.service;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.ontrack.core.model.*;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.core.support.TimeUtils;
import net.ontrack.extension.api.property.PropertiesService;
import net.ontrack.extension.git.*;
import net.ontrack.extension.git.client.*;
import net.ontrack.extension.git.model.*;
import net.ontrack.service.ControlService;
import net.ontrack.service.ManagementService;
import net.ontrack.service.api.ScheduledService;
import net.sf.jstring.Strings;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DefaultGitService implements GitService, GitIndexation, ScheduledService {

    private final Logger logger = LoggerFactory.getLogger(GitService.class);
    private final SecurityUtils securityUtils;
    private final Strings strings;
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
            Strings strings, PropertiesService propertiesService,
            ManagementService managementService,
            ControlService controlService, GitClientFactory gitClientFactory) {
        this.securityUtils = securityUtils;
        this.strings = strings;
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
    public ChangeLogCommits getChangeLogCommits(final Locale locale, ChangeLogSummary summary) {
        // Gets the branch ID
        int branchId = summary.getBranch().getId();
        // Gets the client client for this branch
        GitClient gitClient = getGitClient(branchId);
        // Gets the configuration
        GitConfiguration gitConfiguration = gitClient.getConfiguration();
        // Gets the tag boundaries
        String tagFrom = summary.getBuildFrom().getBuildSummary().getName();
        String tagTo = summary.getBuildTo().getBuildSummary().getName();
        String tagPattern = gitConfiguration.getTag();
        if (StringUtils.isNotBlank(tagPattern)) {
            tagFrom = StringUtils.replace(tagPattern, "*", tagFrom);
            tagTo = StringUtils.replace(tagPattern, "*", tagTo);
        }
        // Gets the commits
        GitLog log = gitClient.log(tagFrom, tagTo);
        // Link?
        String commitLinkValue = propertiesService.getPropertyValue(Entity.PROJECT, summary.getBranch().getProject().getId(), GitExtension.EXTENSION, GitCommitLinkProperty.NAME);
        final String commitLinkFormat;
        if (StringUtils.isNotBlank(commitLinkValue)) {
            commitLinkFormat = StringUtils.replace(commitLinkValue, "*", "%s");
        } else {
            commitLinkFormat = "";
        }
        // OK
        final DateTime now = TimeUtils.now();
        return new ChangeLogCommits(
                new GitUILog(
                        log.getPlot(),
                        Lists.transform(
                                log.getCommits(),
                                new Function<GitCommit, GitUICommit>() {
                                    @Override
                                    public GitUICommit apply(GitCommit commit) {
                                        DateTime time = commit.getCommitTime();
                                        String formattedTime = TimeUtils.format(locale, time);
                                        String elapsedTime = TimeUtils.elapsed(strings, locale, time, now);
                                        return new GitUICommit(
                                                commit,
                                                String.format(commitLinkFormat, commit.getId()),
                                                elapsedTime,
                                                formattedTime
                                        );
                                    }
                                }
                        )
                )
        );
    }

    @Override
    public void run() {
        logger.info("[git] Running the indexation task...");
        List<ProjectSummary> projectList = managementService.getProjectList();
        for (ProjectSummary project : projectList) {
            // Gets the Git remote for this project
            String gitRemote = propertiesService.getPropertyValue(Entity.PROJECT, project.getId(), GitExtension.EXTENSION, GitRemoteProperty.NAME);
            if (StringUtils.isNotBlank(gitRemote)) {
                // List of branches for the project
                List<BranchSummary> branchList = managementService.getBranchList(project.getId());
                for (BranchSummary branch : branchList) {
                    // Logging
                    logger.info(
                            "[git] Running the indexation task for project={}, branch={} ...",
                            project.getName(), branch.getName()
                    );
                    // Indexation for the branch
                    syncBranch(branch.getId());
                    // Logging
                    logger.info(
                            "[git] End of the indexation task for project={}, branch={} ...",
                            project.getName(), branch.getName()
                    );
                }
            }
        }
        logger.info("[git] End of the indexation task.");
    }

    protected void syncBranch(int branchId) {
        getGitClient(branchId).sync();
    }

    @Override
    public Runnable getTask() {
        return this;
    }

    @Override
    public Trigger getTrigger() {
        return new PeriodicTrigger(5, TimeUnit.MINUTES);
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

    protected GitClient getGitClient(int branchId) {
        // Gets the branch Git configuration
        GitConfiguration gitConfiguration = getGitConfiguration(branchId);
        // Checks the configuration
        gitConfiguration = checkGitConfiguration(gitConfiguration);
        // Gets the Git client
        return gitClientFactory.getClient(gitConfiguration);
    }

    protected void doImportBuilds(int branchId, GitImportBuildsForm form) {
        // Gets the branch Git client
        GitClient gitClient = getGitClient(branchId);
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

    private GitConfiguration checkGitConfiguration(GitConfiguration gitConfiguration) {
        if (StringUtils.isBlank(gitConfiguration.getRemote())) {
            throw new GitProjectRemoteNotConfiguredException();
        } else if (StringUtils.isBlank(gitConfiguration.getBranch())) {
            return gitConfiguration.withDefaultBranch();
        } else {
            return gitConfiguration;
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
