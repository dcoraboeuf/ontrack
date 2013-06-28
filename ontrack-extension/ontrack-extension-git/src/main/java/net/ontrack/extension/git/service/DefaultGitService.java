package net.ontrack.extension.git.service;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.ontrack.core.model.*;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.core.support.MessageAnnotationUtils;
import net.ontrack.core.support.MessageAnnotator;
import net.ontrack.core.support.TimeUtils;
import net.ontrack.extension.api.ExtensionManager;
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

import java.util.*;
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
    private final ExtensionManager extensionManager;
    // Threads for the import
    private final ExecutorService executorImportBuilds = Executors.newFixedThreadPool(
            1,
            new ThreadFactoryBuilder()
                    .setDaemon(true)
                    .setNameFormat("git-import-builds-%s")
                    .build());
    private List<GitConfigurator> gitConfigurators;
    private List<GitMessageAnnotator> gitMessageAnnotators;

    @Autowired
    public DefaultGitService(
            SecurityUtils securityUtils,
            Strings strings, PropertiesService propertiesService,
            ManagementService managementService,
            ControlService controlService, GitClientFactory gitClientFactory, ExtensionManager extensionManager) {
        this.securityUtils = securityUtils;
        this.strings = strings;
        this.propertiesService = propertiesService;
        this.managementService = managementService;
        this.controlService = controlService;
        this.gitClientFactory = gitClientFactory;
        this.extensionManager = extensionManager;
    }

    @Autowired(required = false)
    public void setGitConfigurators(List<GitConfigurator> gitConfigurators) {
        this.gitConfigurators = gitConfigurators;
    }

    @Autowired(required = false)
    public void setGitMessageAnnotators(List<GitMessageAnnotator> gitMessageAnnotators) {
        this.gitMessageAnnotators = gitMessageAnnotators;
    }

    @Override
    public boolean isGitConfigured(int branchId) {
        GitConfiguration configuration = getGitConfiguration(branchId);
        return configuration.isValid();
    }

    @Override
    public boolean isCommitDefined(String commit) {
        // Gets the list of projects
        List<ProjectSummary> projectList = managementService.getProjectList();
        for (ProjectSummary project : projectList) {
            int projectId = project.getId();
            List<BranchSummary> branchList = managementService.getBranchList(projectId);
            for (BranchSummary branch : branchList) {
                int branchId = branch.getId();
                GitConfiguration configuration = getGitConfiguration(branchId);
                if (configuration.isValid()) {
                    // Gets the client client for this branch
                    GitClient gitClient = getGitClient(branchId);
                    // Is the commit defined for this branch?
                    if (gitClient.isCommitDefined(commit)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public GitCommitInfo getCommitInfo(Locale locale, String commit) {
        GitCommit theGitCommit = null;
        BranchSummary theBranch = null;
        GitConfiguration theConfiguration = null;
        Collection<BuildInfo> buildSummaries = new ArrayList<>();
        List<BranchPromotions> revisionPromotionsPerBranch = new ArrayList<>();
        // For all projects & branches
        List<ProjectSummary> projectList = managementService.getProjectList();
        for (ProjectSummary projectSummary : projectList) {
            List<BranchSummary> branchList = managementService.getBranchList(projectSummary.getId());
            for (BranchSummary branchSummary : branchList) {
                int branchId = branchSummary.getId();
                final GitConfiguration configuration = getGitConfiguration(branchId);
                if (configuration.isValid()) {
                    // Gets the client client for this branch
                    GitClient gitClient = getGitClient(branchId);
                    // Gets the commit
                    GitCommit gitCommit = gitClient.getCommitFor(commit);
                    if (gitCommit != null) {
                        // Reference commit
                        if (theGitCommit == null) {
                            theGitCommit = gitCommit;
                            theBranch = branchSummary;
                            theConfiguration = configuration;
                        }
                        // Gets the earliest tag on this branch that contains this commit
                        String tagName = gitClient.getEarliestTagForCommit(
                                gitCommit.getId(),
                                new Predicate<String>() {
                                    @Override
                                    public boolean apply(String tagName) {
                                        return configuration.isValidTagName(tagName);
                                    }
                                }
                        );
                        // If a tag is provided, gets the corresponding build name
                        if (StringUtils.isNotBlank(tagName)) {
                            // Gets the build name from the tag (we usually do otherwise)
                            String buildName = configuration.getBuildNameFromTagName(tagName);
                            // Gets the build from the ontrack database
                            Integer buildId = managementService.findBuildByName(branchId, buildName);
                            // Build found
                            if (buildId != null) {
                                // Gets the build information
                                BuildSummary buildSummary = managementService.getBuild(buildId);
                                // Gets the promotion levels & validation stamps
                                List<BuildPromotionLevel> promotionLevels = managementService.getBuildPromotionLevels(locale, buildId);
                                List<BuildValidationStamp> buildValidationStamps = managementService.getBuildValidationStamps(locale, buildId);
                                // Adds to the list
                                buildSummaries.add(
                                        new BuildInfo(
                                                buildSummary,
                                                promotionLevels,
                                                buildValidationStamps
                                        ));
                                // Gets the promotions for this branch
                                List<Promotion> promotions = managementService.getPromotionsForBranch(locale, branchId, buildId);
                                if (promotions != null && !promotions.isEmpty()) {
                                    revisionPromotionsPerBranch.add(new BranchPromotions(
                                            managementService.getBranch(branchId),
                                            promotions
                                    ));
                                }
                            }
                        }
                    }
                }
            }
        }
        // Found
        if (theGitCommit != null) {
            // UI Commit
            GitUICommit uiCommit = toUICommits(
                    locale,
                    theBranch, // Cannot be null since theGitCommit is not null
                    theConfiguration, // Cannot be null since theGitCommit is not null
                    Arrays.asList(theGitCommit)).get(0);
            // OK
            return new GitCommitInfo(
                    uiCommit,
                    buildSummaries,
                    revisionPromotionsPerBranch
            );
        }
        // Not found
        else {
            throw new GitCommitNotFoundException(commit);
        }
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
    public ChangeLogCommits getChangeLogCommits(final Locale locale, final ChangeLogSummary summary) {
        // Gets the branch ID
        final BranchSummary branch = summary.getBranch();
        int branchId = branch.getId();
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
        List<GitCommit> commits = log.getCommits();
        List<GitUICommit> uiCommits = toUICommits(locale, branch, gitConfiguration, commits);
        return new ChangeLogCommits(
                new GitUILog(
                        log.getPlot(),
                        uiCommits
                )
        );
    }

    protected List<GitUICommit> toUICommits(final Locale locale, final BranchSummary branch, GitConfiguration gitConfiguration, List<GitCommit> commits) {
        // Link?
        String commitLinkValue = gitConfiguration.getCommitLink();
        final String commitLinkFormat;
        if (StringUtils.isNotBlank(commitLinkValue)) {
            commitLinkFormat = StringUtils.replace(commitLinkValue, "*", "%s");
        } else {
            commitLinkFormat = "";
        }
        // OK
        final DateTime now = TimeUtils.now();
        return Lists.transform(
                commits,
                new Function<GitCommit, GitUICommit>() {
                    @Override
                    public GitUICommit apply(GitCommit commit) {
                        // Times
                        DateTime time = commit.getCommitTime();
                        String formattedTime = TimeUtils.format(locale, time);
                        String elapsedTime = TimeUtils.elapsed(strings, locale, time, now);
                        // Annotated message
                        List<String> annotatedMessages = MessageAnnotationUtils.annotate(
                                Arrays.asList(
                                        commit.getShortMessage(),
                                        commit.getFullMessage()
                                ),
                                Lists.transform(
                                        gitMessageAnnotators,
                                        new Function<GitMessageAnnotator, MessageAnnotator>() {
                                            @Override
                                            public MessageAnnotator apply(GitMessageAnnotator gitMessageAnnotator) {
                                                return gitMessageAnnotator.annotator(branch);
                                            }
                                        }
                                ));
                        // OK
                        return new GitUICommit(
                                commit,
                                annotatedMessages.get(0),
                                annotatedMessages.get(1),
                                String.format(commitLinkFormat, commit.getId()),
                                elapsedTime,
                                formattedTime
                        );
                    }
                }
        );
    }

    @Override
    public ChangeLogFiles getChangeLogFiles(Locale locale, ChangeLogSummary summary) {
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
        // Diff
        final GitDiff diff = gitClient.diff(tagFrom, tagTo);
        // File change links
        String fileChangeLinkValue = gitConfiguration.getFileAtCommitLink();
        final String fileChangeLinkFormat;
        if (StringUtils.isNotBlank(fileChangeLinkValue)) {
            fileChangeLinkFormat = StringUtils.replace(
                    StringUtils.replace(fileChangeLinkValue, "$", "%2$s"),
                    "*",
                    "%1$s");
        } else {
            fileChangeLinkFormat = "";
        }
        // OK
        return new ChangeLogFiles(
                Lists.transform(
                        diff.getEntries(),
                        new Function<GitDiffEntry, ChangeLogFile>() {
                            @Override
                            public ChangeLogFile apply(GitDiffEntry entry) {
                                return toChangeLogFile(entry).withUrl(
                                        String.format(
                                                fileChangeLinkFormat,
                                                entry.getReferenceId(diff.getFrom(), diff.getTo()),
                                                entry.getReferencePath()
                                        )
                                );
                            }
                        }
                )
        );
    }

    private ChangeLogFile toChangeLogFile(GitDiffEntry entry) {
        switch (entry.getChangeType()) {
            case ADD:
                return ChangeLogFile.of(GitChangeType.ADD, entry.getNewPath());
            case COPY:
                return ChangeLogFile.of(GitChangeType.COPY, entry.getOldPath(), entry.getNewPath());
            case DELETE:
                return ChangeLogFile.of(GitChangeType.DELETE, entry.getOldPath());
            case MODIFY:
                return ChangeLogFile.of(GitChangeType.MODIFY, entry.getOldPath());
            case RENAME:
                return ChangeLogFile.of(GitChangeType.RENAME, entry.getOldPath(), entry.getNewPath());
            default:
                throw new IllegalArgumentException("Unknown change type: " + entry.getChangeType());
        }
    }

    @Override
    public void run() {
        logger.info("[git] Running the indexation task...");
        List<ProjectSummary> projectList = managementService.getProjectList();
        for (ProjectSummary project : projectList) {
            // List of branches for the project
            List<BranchSummary> branchList = managementService.getBranchList(project.getId());
            for (BranchSummary branch : branchList) {
                // Gets the configuration for this branch
                GitConfiguration configuration = getGitConfiguration(branch.getId());
                if (configuration.withDefaults().isValid()) {
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
        return new Runnable() {
            @Override
            public void run() {
                if (extensionManager.isExtensionEnabled(GitExtension.EXTENSION)) {
                    DefaultGitService.this.run();
                }
            }
        };
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
                // Build name
                logger.info("[git] Creating build for tag {}", tagName);
                String buildName = matcher.group(1);
                logger.info("[git] Build {} from tag {}", buildName, tagName);
                // Existing build?
                boolean createBuild;
                Integer buildId = managementService.findBuildByName(branchId, buildName);
                if (buildId != null) {
                    if (form.isOverride()) {
                        // Deletes the build
                        logger.info("[git] Deleting existing build {}", buildName);
                        createBuild = managementService.deleteBuild(buildId).isSuccess();
                    } else {
                        // Keeps the build
                        logger.info("[git] Build {} already exists", buildName);
                        createBuild = false;
                    }
                } else {
                    createBuild = true;
                }
                // Actual creation
                if (createBuild) {
                    logger.info("[git] Creating build {} from tag {}", buildName, tagName);
                    controlService.createBuild(branchId, new BuildCreationForm(
                            buildName,
                            "Imported from Git tag " + tagName,
                            PropertiesCreationForm.create()
                    ));
                }
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

    @Override
    public GitConfiguration getGitConfiguration(int branchId) {
        // Gets the branch
        BranchSummary branch = managementService.getBranch(branchId);
        // Project Id
        int projectId = branch.getProject().getId();
        // Empty configuration
        GitConfiguration configuration = GitConfiguration.empty();
        // Configurators
        if (gitConfigurators != null) {
            for (GitConfigurator gitConfigurator : gitConfigurators) {
                configuration = gitConfigurator.configure(configuration, branch);
            }
        }
        // Properties
        configuration = configuration
                .withRemote(propertiesService.getPropertyValue(Entity.PROJECT, projectId, GitExtension.EXTENSION, GitRemoteProperty.NAME))
                .withBranch(propertiesService.getPropertyValue(Entity.BRANCH, branchId, GitExtension.EXTENSION, GitBranchProperty.NAME))
                .withTag(propertiesService.getPropertyValue(Entity.BRANCH, branchId, GitExtension.EXTENSION, GitTagProperty.NAME))
                .withCommitLink(propertiesService.getPropertyValue(Entity.PROJECT, projectId, GitExtension.EXTENSION, GitCommitLinkProperty.NAME))
                .withFileAtCommitLink(propertiesService.getPropertyValue(Entity.PROJECT, projectId, GitExtension.EXTENSION, GitFileAtCommitLinkProperty.NAME));
        // Defaults
        configuration = configuration.withDefaults();
        // OK
        return configuration;
    }
}
