package net.ontrack.extension.svnexplorer.service;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.ontrack.core.model.*;
import net.ontrack.extension.api.property.PropertiesService;
import net.ontrack.extension.issue.*;
import net.ontrack.extension.jira.JIRAService;
import net.ontrack.extension.jira.service.JIRAIssueNotFoundException;
import net.ontrack.extension.jira.service.model.JIRAIssue;
import net.ontrack.extension.svn.SubversionExtension;
import net.ontrack.extension.svn.SubversionPathPropertyExtension;
import net.ontrack.extension.svn.service.SubversionService;
import net.ontrack.extension.svn.service.model.*;
import net.ontrack.extension.svn.support.SVNLogEntryCollector;
import net.ontrack.extension.svn.support.SVNUtils;
import net.ontrack.extension.svnexplorer.ProjectRootPathPropertyExtension;
import net.ontrack.extension.svnexplorer.SVNExplorerExtension;
import net.ontrack.extension.svnexplorer.SensibleFilesPropertyExtension;
import net.ontrack.extension.svnexplorer.model.*;
import net.ontrack.service.ManagementService;
import net.ontrack.tx.Transaction;
import net.ontrack.tx.TransactionService;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.util.*;
import java.util.regex.Pattern;

@Service
public class DefaultSVNExplorerService implements SVNExplorerService {

    private final Logger logger = LoggerFactory.getLogger(SVNExplorerService.class);
    private final ManagementService managementService;
    private final PropertiesService propertiesService;
    private final SubversionService subversionService;
    private final IssueServiceFactory issueServiceFactory;
    // FIXME Removes the reference to JIRA
    private final JIRAService jiraService;
    private final TransactionService transactionService;

    /**
     * Function that extracts the path from a SVN location
     */
    private final Function<SVNReference, String> referencePathFn = new Function<SVNReference, String>() {
        @Override
        public String apply(SVNReference reference) {
            return reference.getPath();
        }
    };

    @Autowired
    public DefaultSVNExplorerService(ManagementService managementService, PropertiesService propertiesService, SubversionService subversionService, IssueServiceFactory issueServiceFactory, JIRAService jiraService, TransactionService transactionService) {
        this.managementService = managementService;
        this.propertiesService = propertiesService;
        this.subversionService = subversionService;
        this.issueServiceFactory = issueServiceFactory;
        this.jiraService = jiraService;
        this.transactionService = transactionService;
    }

    @Override
    @Transactional(readOnly = true)
    public ChangeLogSummary getChangeLogSummary(Locale locale, int branchId, int from, int to) {
        try (Transaction ignored = transactionService.start()) {
            // Gets the branch
            BranchSummary branch = managementService.getBranch(branchId);
            // Gets the SVN repository for this branch
            SVNRepository repository = subversionService.getRepositoryForProject(branch.getProject().getId());
            // Gets the build information
            SVNBuild buildFrom = getBuild(repository, locale, from);
            SVNBuild buildTo = getBuild(repository, locale, to);
            // OK
            return new ChangeLogSummary(
                    UUID.randomUUID().toString(),
                    branch,
                    repository,
                    buildFrom,
                    buildTo
            );
        }
    }

    protected Collection<ChangeLogReference> getChangeLogReferences(ChangeLogSummary summary) {

        // Gets the two histories
        SVNHistory historyFrom = summary.getBuildFrom().getHistory();
        SVNHistory historyTo = summary.getBuildTo().getHistory();

        // Sort them from->to with 'to' having the highest revision
        {
            long fromRevision = historyFrom.getReferences().get(0).getRevision();
            long toRevision = historyTo.getReferences().get(0).getRevision();
            if (toRevision < fromRevision) {
                SVNHistory tmp = historyTo;
                historyTo = historyFrom;
                historyFrom = tmp;
            }
        }

        // Indexation of the 'from' history using the paths
        Map<String, SVNReference> historyFromIndex = Maps.uniqueIndex(
                historyFrom.getReferences(),
                referencePathFn
        );

        // List of ranges to collect
        List<ChangeLogReference> references = new ArrayList<>();

        // For each reference on the 'to' history
        for (SVNReference toReference : historyTo.getReferences()) {
            // Collects a range of revisions
            long toRevision = toReference.getRevision();
            long fromRevision = 0;
            // Gets any 'from' reference
            SVNReference fromReference = historyFromIndex.get(toReference.getPath());
            if (fromReference != null) {
                fromRevision = fromReference.getRevision();
                if (fromRevision > toRevision) {
                    long t = toRevision;
                    toRevision = fromRevision;
                    fromRevision = t;
                }
            }
            // Adds this reference
            references.add(new ChangeLogReference(
                    toReference.getPath(),
                    fromRevision,
                    toRevision
            ));
        }

        // OK
        return references;
    }

    @Override
    @Transactional(readOnly = true)
    public ChangeLogRevisions getChangeLogRevisions(ChangeLogSummary summary) {

        // Reference
        Collection<ChangeLogReference> references = getChangeLogReferences(summary);

        // No difference?
        if (references.isEmpty()) {
            return ChangeLogRevisions.none();
        }

        // SVN transaction
        try (Transaction ignored = transactionService.start()) {
            List<ChangeLogRevision> revisions = new ArrayList<>();
            for (ChangeLogReference reference : references) {
                if (!reference.isNone()) {
                    // List of log entries
                    SVNLogEntryCollector logEntryCollector = new SVNLogEntryCollector();
                    // SVN change log
                    subversionService.log(
                            summary.getRepository(),
                            SVNUtils.toURL(subversionService.getURL(summary.getRepository(), reference.getPath())),
                            SVNRevision.create(reference.getEnd()),
                            SVNRevision.create(reference.getStart()),
                            SVNRevision.create(reference.getEnd()),
                            true, // Stops on copy
                            false, // No path discovering (yet)
                            0L, // no limit
                            true, // Includes merged revisions
                            logEntryCollector
                    );
                    // Loops through all SVN log entries, taking the merged revisions into account
                    int level = 0;
                    for (SVNLogEntry svnEntry : logEntryCollector.getEntries()) {
                        long revision = svnEntry.getRevision();
                        if (SVNRevision.isValidRevisionNumber(revision)) {
                            // Conversion
                            ChangeLogRevision entry = createChangeLogRevision(summary.getRepository(), reference.getPath(), level, svnEntry);
                            // Adds it to the list
                            revisions.add(entry);
                            // New parent?
                            if (svnEntry.hasChildren()) {
                                level++;
                            }
                        } else {
                            level--;
                        }
                    }
                }
            }
            // OK
            return new ChangeLogRevisions(revisions);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ChangeLogIssues getChangeLogIssues(ChangeLogSummary summary, ChangeLogRevisions revisions) {
        // In a SVN/JIRA transaction
        try (Transaction ignored = transactionService.start()) {
            // Index of issues, sorted by keys
            Map<String, ChangeLogIssue> issues = new TreeMap<>();
            // For all revisions in this revision log
            for (ChangeLogRevision changeLogRevision : revisions.getList()) {
                long revision = changeLogRevision.getRevision();
                collectIssuesForRevision(summary.getRepository(), issues, revision);
            }
            // List of issues
            List<ChangeLogIssue> issuesList = new ArrayList<>(issues.values());
            // TODO Validation
            // validationService.validate(changeLog, issuesList);
            // OK
            // FIXME Removes link from JIRA
            return new ChangeLogIssues(null, issuesList);

        }
    }

    @Override
    @Transactional(readOnly = true)
    public ChangeLogFiles getChangeLogFiles(ChangeLogSummary summary, ChangeLogRevisions revisions) {
        // In a SVN/JIRA transaction
        try (Transaction ignored = transactionService.start()) {
            // Index of files, indexed by path
            Map<String, ChangeLogFile> files = new TreeMap<>();
            // For each revision
            for (ChangeLogRevision changeLogRevision : revisions.getList()) {
                // Takes into account only the unmerged revisions
                if (changeLogRevision.getLevel() == 0) {
                    long revision = changeLogRevision.getRevision();
                    collectFilesForRevision(summary.getRepository(), files, revision);
                }
            }
            // List of files
            List<ChangeLogFile> filesList = new ArrayList<>(files.values());
            // OK
            return new ChangeLogFiles(filesList);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ChangeLogInfo getChangeLogInfo(ChangeLogSummary summary, ChangeLogIssues issues, ChangeLogFiles files) {
        // List of patterns for sensible files
        String sensibleFilesPattern = propertiesService.getPropertyValue(Entity.BRANCH, summary.getBranch().getId(), SVNExplorerExtension.EXTENSION, SensibleFilesPropertyExtension.NAME);
        // Filtering function
        Predicate<ChangeLogFile> sensibleFilePredicate = getSensibleFilePredicateFn(sensibleFilesPattern);
        // JIRA issue statuses
        Map<String, ChangeLogInfoStatus> statuses = new TreeMap<>();
        for (ChangeLogIssue changeLogIssue : issues.getList()) {
            IssueStatus status = changeLogIssue.getIssue().getStatus();
            String statusName = status.getName();
            ChangeLogInfoStatus infoStatus = statuses.get(statusName);
            if (infoStatus != null) {
                infoStatus.incr();
            } else {
                infoStatus = new ChangeLogInfoStatus(status);
                statuses.put(statusName, infoStatus);
            }
        }
        // Sensible files
        List<ChangeLogFile> sensibleFiles = Lists.newArrayList(
                Iterables.filter(
                        files.getList(),
                        sensibleFilePredicate
                )
        );
        // OK
        return new ChangeLogInfo(
                Lists.newArrayList(statuses.values()),
                sensibleFiles
        );
    }

    @Override
    @Transactional(readOnly = true)
    public RevisionInfo getRevisionInfo(Locale locale, long revision) {
        // FIXME SVN Repository
        // Gets information about the revision
        SVNRevisionInfo basicInfo = subversionService.getRevisionInfo(null, revision);
        // FIXME SVN Repository
        ChangeLogRevision changeLogRevision = createChangeLogRevision(
                null,
                basicInfo.getPath(),
                0,
                revision,
                basicInfo.getMessage(),
                basicInfo.getAuthor(),
                basicInfo.getDateTime()
        );

        // Gets the first copy event on this path after this revision
        // FIXME SVN Repository
        SVNLocation firstCopy = subversionService.getFirstCopyAfter(null, basicInfo.toLocation());

        // Data to collect
        Collection<BuildInfo> buildSummaries = new ArrayList<>();
        List<BranchPromotions> revisionPromotionsPerBranch = new ArrayList<>();
        // Loops over all branches
        List<ProjectSummary> projectList = managementService.getProjectList();
        for (ProjectSummary projectSummary : projectList) {
            List<BranchSummary> branchList = managementService.getBranchList(projectSummary.getId());
            for (BranchSummary branchSummary : branchList) {
                int branchId = branchSummary.getId();
                // Identifies a possible build given the path/revision and the first copy
                Integer buildId = lookupBuild(basicInfo.toLocation(), firstCopy, branchSummary.getId());
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

        // OK
        return new RevisionInfo(
                changeLogRevision,
                buildSummaries,
                revisionPromotionsPerBranch
        );
    }

    private Integer lookupBuild(SVNLocation location, SVNLocation firstCopy, int branchId) {
        // Gets the build path pattern for the branch
        String buildPathPattern = propertiesService.getPropertyValue(
                Entity.BRANCH,
                branchId,
                SubversionExtension.EXTENSION,
                SubversionExtension.SUBVERSION_BUILD_PATH
        );
        // If not build path is defined, cannot find any build
        if (StringUtils.isBlank(buildPathPattern)) {
            return null;
        }
        // Revision path
        else if (SVNExplorerPathUtils.isPathRevision(buildPathPattern)) {
            return getEarliestBuild(branchId, location, buildPathPattern);
        }
        // Tag pattern
        else {
            // Uses the copy (if available)
            if (firstCopy != null) {
                return getEarliestBuild(branchId, firstCopy, buildPathPattern);
            } else {
                return null;
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public IssueInfo getIssueInfo(Locale locale, String key) {
        // Gets the details about the issue
        // FIXME JIRA Configuration
        JIRAIssue issue = jiraService.getIssue(null, key);
        // Gets the list of revisions & their basic info (order from latest to oldest)
        List<ChangeLogRevision> revisions = Lists.transform(
                // FIXME SVN Repository
                subversionService.getRevisionsForIssueKey(null, key),
                new Function<Long, ChangeLogRevision>() {
                    @Override
                    public ChangeLogRevision apply(Long revision) {
                        // FIXME SVN Repository
                        SVNRevisionInfo basicInfo = subversionService.getRevisionInfo(null, revision);
                        return createChangeLogRevision(
                                // FIXME SVN Repository
                                null,
                                basicInfo.getPath(),
                                0,
                                revision,
                                basicInfo.getMessage(),
                                basicInfo.getAuthor(),
                                basicInfo.getDateTime()
                        );
                    }
                });
        // Gets the last revision (which is the first in the list)
        ChangeLogRevision firstRevision = revisions.get(0);
        RevisionInfo revisionInfo = getRevisionInfo(locale, firstRevision.getRevision());
        // Merged revisions
        // FIXME SVN Repository
        List<Long> merges = subversionService.getMergesForRevision(null, revisionInfo.getChangeLogRevision().getRevision());
        List<RevisionInfo> mergedRevisionInfos = new ArrayList<>();
        Set<String> paths = new HashSet<>();
        for (long merge : merges) {
            // Gets the revision info
            RevisionInfo mergeRevisionInfo = getRevisionInfo(locale, merge);
            // If the information contains as least one build, adds it
            if (!mergeRevisionInfo.getBuilds().isEmpty()) {
                // Keeps only the first one for a given target path
                String path = mergeRevisionInfo.getChangeLogRevision().getPath();
                if (!paths.contains(path)) {
                    mergedRevisionInfos.add(mergeRevisionInfo);
                    paths.add(path);
                }
            }
        }
        // OK
        return new IssueInfo(
                issue,
                subversionService.formatRevisionTime(issue.getUpdateTime()),
                revisionInfo,
                mergedRevisionInfos,
                revisions
        );
    }

    @Override
    @Transactional(readOnly = true)
    public BranchHistory getBranchHistory(int projectId, Locale locale) {
        try (Transaction ignored = transactionService.start()) {
            logger.debug("[branch-history] Start");
            // Gets the project details
            ProjectSummary project = managementService.getProject(projectId);
            // Gets the SVN repository for this project
            final SVNRepository repository = subversionService.getRepositoryForProject(projectId);
            // Gets the root path for this project
            String rootPath = propertiesService.getPropertyValue(Entity.PROJECT, projectId, SVNExplorerExtension.EXTENSION, ProjectRootPathPropertyExtension.NAME);
            if (StringUtils.isBlank(rootPath)) {
                throw new ProjectHasRootPathException(project.getName());
            }
            // Gets the latest revision on this root path
            long rootRevision = subversionService.getRepositoryRevision(repository, SVNUtils.toURL(subversionService.getURL(null, rootPath)));
            SVNLocation rootLocation = new SVNLocation(rootPath, rootRevision);
            // Tree of locations
            SVNTreeNode rootNode = new SVNTreeNode(rootLocation);
            // Stack of locations
            Stack<SVNTreeNode> stack = new Stack<>();
            stack.add(rootNode);
            // Trimming the stack
            while (!stack.isEmpty()) {
                // Gets the top
                SVNTreeNode current = stack.pop();
                // Gets all the copies from this location
                Collection<SVNLocation> copies = subversionService.getCopiesFrom(
                        repository,
                        current.getLocation().withRevision(1),
                        SVNLocationSortMode.FROM_NEWEST);
                // No copy?
                if (copies.isEmpty()) {
                    // Trunk or branch
                    if (subversionService.isTrunkOrBranch(repository, current.getLocation().getPath())) {
                        // Attaches to the parent
                        current.attachToParent();
                    }
                }
                // At least one copy
                else {
                    // Attach to the parent
                    current.attachToParent();
                    // For each copy
                    for (SVNLocation copy : copies) {
                        // Adds to the stack
                        stack.push(new SVNTreeNode(current, copy));
                    }
                }
            }

            // Pruning the closed branches
            rootNode.visitBottomUp(new SVNTreeNodeVisitor() {
                @Override
                public void visit(SVNTreeNode node) {
                    if (subversionService.isTrunkOrBranch(repository, node.getLocation().getPath())) {
                        node.setClosed(subversionService.isClosed(repository, node.getLocation().getPath()));
                    }
                    // Loops over children
                    node.filterChildren(new Predicate<SVNTreeNode>() {

                        @Override
                        public boolean apply(SVNTreeNode child) {
                            return !child.isClosed();
                        }
                    });
                }
            });

            // Prunes the tag-only locations
            rootNode.visitBottomUp(new SVNTreeNodeVisitor() {

                @Override
                public void visit(SVNTreeNode node) {
                    // Is this node a tag?
                    node.setTag(subversionService.isTag(repository, node.getLocation().getPath()));
                    // Loops over children
                    node.filterChildren(new Predicate<SVNTreeNode>() {

                        @Override
                        public boolean apply(SVNTreeNode child) {
                            boolean allTags = child.all(new Predicate<SVNTreeNode>() {

                                @Override
                                public boolean apply(SVNTreeNode n) {
                                    return n.isTag();
                                }
                            });
                            return !allTags;
                        }
                    });
                }
            });

            // Collects history
            BranchHistoryLine root = collectHistory(repository, locale, rootNode);

            // OK
            logger.debug("[branch-history] End");
            return new BranchHistory(
                    project,
                    root
            );
        }
    }

    @Override
    public boolean isSvnExplorerConfigured(int branchId) {
        String buildPathPattern = propertiesService.getPropertyValue(Entity.BRANCH, branchId, SubversionExtension.EXTENSION, SubversionExtension.SUBVERSION_BUILD_PATH);
        return StringUtils.isNotBlank(buildPathPattern);
    }

    private BranchHistoryLine collectHistory(SVNRepository repository, Locale locale, SVNTreeNode node) {
        // Line itself
        BranchHistoryLine line = createBranchHistoryLine(repository, locale, node.getLocation());
        // Collects lines
        for (SVNTreeNode childNode : node.getChildren()) {
            line.addLine(collectHistory(repository, locale, childNode));
        }
        // OK
        return line;
    }

    private BranchHistoryLine createBranchHistoryLine(SVNRepository repository, final Locale locale, SVNLocation location) {
        // Core
        BranchHistoryLine line = new BranchHistoryLine(
                subversionService.getReference(repository, location),
                subversionService.isTag(repository, location.getPath())
        );
        // Branch?
        Collection<Integer> branchIds = propertiesService.findEntityByPropertyValue(Entity.BRANCH, SubversionExtension.EXTENSION, SubversionPathPropertyExtension.PATH, location.getPath());
        if (branchIds.size() > 1) {
            throw new IllegalStateException("At most one branch should be eligible - configuration problem at branch level?");
        } else if (branchIds.size() == 1) {
            final int branchId = branchIds.iterator().next();
            line = line.withBranchLastStatus(managementService.getBranchLastStatus(locale, branchId));
        }
        // OK
        return line;
    }

    /**
     * Gets the earliest build that contains the given <code>location</code> on this branch.
     *
     * @param branchId    ID of the branch
     * @param location    Location for the revision to search the build for
     * @param pathPattern Path pattern for the build on this branch
     * @return ID of the earliest build that contains this revision, or <code>null</code> if none is found
     */
    private Integer getEarliestBuild(int branchId, SVNLocation location, String pathPattern) {
        if (SVNExplorerPathUtils.followsBuildPattern(location, pathPattern)) {
            // Gets the build name
            String buildName = SVNExplorerPathUtils.getBuildName(location, pathPattern);
            /**
             * If the build is defined by path@revision, the earliest build is the one
             * that follows this revision.
             */
            if (SVNExplorerPathUtils.isPathRevision(pathPattern)) {
                return managementService.findBuildAfterUsingNumericForm(branchId, buildName);
            }
            /**
             * In any other case (tag or tag prefix), the build must be looked exactly
             */
            else {
                return managementService.findBuildByName(branchId, buildName);
            }
        } else {
            return null;
        }
    }

    private Predicate<ChangeLogFile> getSensibleFilePredicateFn(String sensibleFilesPatterns) {
        // Empty?
        if (StringUtils.isBlank(sensibleFilesPatterns)) {
            return Predicates.alwaysFalse();
        }
        // List of patterns
        final List<Pattern> patterns = new ArrayList<>();
        // Split on lines
        String[] lines = StringUtils.split(sensibleFilesPatterns, "\n\r");
        // Creates the patterns
        for (String line : lines) {
            if (StringUtils.isNotBlank(line)) {
                Pattern pattern = Pattern.compile(StringUtils.trim(line));
                patterns.add(pattern);
            }
        }
        // OK
        return new Predicate<ChangeLogFile>() {
            @Override
            public boolean apply(ChangeLogFile file) {
                String path = file.getPath();
                for (Pattern pattern : patterns) {
                    if (pattern.matcher(path).matches()) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    private void collectFilesForRevision(SVNRepository repository, Map<String, ChangeLogFile> files, long revision) {
        SVNRevisionPaths revisionPaths = subversionService.getRevisionPaths(repository, revision);
        for (SVNRevisionPath revisionPath : revisionPaths.getPaths()) {
            String path = revisionPath.getPath();
            // Existing file entry?
            ChangeLogFile changeLogFile = files.get(path);
            if (changeLogFile == null) {
                changeLogFile = new ChangeLogFile(path, subversionService.getBrowsingURL(repository, path));
                files.put(path, changeLogFile);
            }
            // Adds the revision and the type
            ChangeLogFileChange change = new ChangeLogFileChange(
                    revisionPaths.getInfo(),
                    revisionPath.getChangeType(),
                    subversionService.getFileChangeBrowsingURL(repository, path, revisionPaths.getInfo().getRevision())
            );
            changeLogFile.addChange(change);
        }
    }

    private void collectIssuesForRevision(SVNRepository repository, Map<String, ChangeLogIssue> issues, long revision) {
        // Gets all issues attached to this revision
        List<String> issueKeys = subversionService.getIssueKeysForRevision(repository, revision);
        // For each issue
        for (String issueKey : issueKeys) {
            // Gets its details if not indexed yet
            ChangeLogIssue changeLogIssue = issues.get(issueKey);
            if (changeLogIssue == null) {
                changeLogIssue = getChangeLogIssue(repository, issueKey);
            }
            // Existing issue?
            if (changeLogIssue != null) {
                // Attaches the revision to this issue
                SVNRevisionInfo issueRevision = subversionService.getRevisionInfo(repository, revision);
                changeLogIssue = changeLogIssue.addRevision(issueRevision);
                // Puts back into the cache
                issues.put(issueKey, changeLogIssue);
            }
        }
    }

    private ChangeLogIssue getChangeLogIssue(SVNRepository repository, String issueKey) {
        // Issue service
        Optional<IssueService> issueService = issueServiceFactory.getOptionalServiceByName(repository.getIssueServiceName());
        // Gets the details about the issue
        if (issueService.isPresent()) {
            try {
                IssueServiceConfig issueServiceConfig = issueService.get().getConfigurationById(repository.getIssueServiceConfigId());
                Issue issue = issueService.get().getIssue(issueServiceConfig, issueKey);
                if (issue == null || StringUtils.isBlank(issue.getKey())) {
                    return null;
                }
                // Creates the issue details for the change logs
                return new ChangeLogIssue(issue, subversionService.formatRevisionTime(issue.getUpdateTime()));
            } catch (JIRAIssueNotFoundException ex) {
                return null;
            }
        } else {
            return null;
        }
    }

    private ChangeLogRevision createChangeLogRevision(SVNRepository repository, String path, int level, SVNLogEntry svnEntry) {
        return createChangeLogRevision(
                repository,
                path,
                level,
                svnEntry.getRevision(),
                svnEntry.getMessage(),
                svnEntry.getAuthor(),
                new DateTime(svnEntry.getDate()));

    }

    private ChangeLogRevision createChangeLogRevision(SVNRepository repository, String path, int level, long revision, String message, String author, DateTime revisionDate) {
        // Issue service
        Optional<IssueService> issueService = issueServiceFactory.getOptionalServiceByName(repository.getIssueServiceName());
        // Formatted message
        String formattedMessage;
        if (issueService.isPresent()) {
            IssueServiceConfig issueServiceConfig = issueService.get().getConfigurationById(repository.getIssueServiceConfigId());
            formattedMessage = issueService.get().formatIssuesInMessage(issueServiceConfig, message);
        } else {
            formattedMessage = message;
        }
        // Revision URL
        String revisionUrl = subversionService.getRevisionBrowsingURL(repository, revision);
        // OK
        return new ChangeLogRevision(
                path,
                level,
                revision,
                author,
                subversionService.formatRevisionTime(revisionDate),
                message,
                revisionUrl,
                formattedMessage);
    }

    private SVNBuild getBuild(SVNRepository repository, Locale locale, int buildId) {
        // Gets the build basic information
        BuildSummary build = managementService.getBuild(buildId);
        // Gets the build SVN tag
        String buildPath = getBuildPath(build.getBranch().getId(), build.getName());
        // Gets the history for this tag using the SubversionService
        SVNHistory history = subversionService.getHistory(repository, buildPath);
        // OK
        return new SVNBuild(
                build,
                history,
                managementService.getBuildValidationStamps(locale, build.getId()),
                managementService.getBuildPromotionLevels(locale, build.getId())
        );
    }

    private String getBuildPath(int branchId, String buildName) {
        // Gets the SVN build path property for the branch
        String buildPathPattern = propertiesService.getPropertyValue(Entity.BRANCH, branchId, SubversionExtension.EXTENSION, SubversionExtension.SUBVERSION_BUILD_PATH);
        if (buildPathPattern == null) {
            throw new SVNExplorerNoBuildPathDefinedForBranchException();
        }
        // Replaces the * by the build name
        return buildPathPattern.replace("*", buildName);
    }
}
