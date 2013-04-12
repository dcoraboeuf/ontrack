package net.ontrack.extension.svnexplorer.service;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.ontrack.core.model.BranchSummary;
import net.ontrack.core.model.BuildSummary;
import net.ontrack.core.model.Entity;
import net.ontrack.extension.api.property.PropertiesService;
import net.ontrack.extension.jira.JIRAService;
import net.ontrack.extension.jira.service.JIRAIssueNotFoundException;
import net.ontrack.extension.jira.service.model.JIRAIssue;
import net.ontrack.extension.svn.SubversionExtension;
import net.ontrack.extension.svn.service.SubversionService;
import net.ontrack.extension.svn.service.model.SVNHistory;
import net.ontrack.extension.svn.service.model.SVNReference;
import net.ontrack.extension.svn.service.model.SVNRevisionInfo;
import net.ontrack.extension.svn.support.SVNLogEntryCollector;
import net.ontrack.extension.svn.support.SVNUtils;
import net.ontrack.extension.svnexplorer.model.*;
import net.ontrack.service.ManagementService;
import net.ontrack.tx.Transaction;
import net.ontrack.tx.TransactionService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.util.*;

@Service
public class DefaultSVNExplorerService implements SVNExplorerService {

    private final ManagementService managementService;
    private final PropertiesService propertiesService;
    private final SubversionService subversionService;
    private final JIRAService jiraService;
    private final TransactionService transactionService;

    @Autowired
    public DefaultSVNExplorerService(ManagementService managementService, PropertiesService propertiesService, SubversionService subversionService, JIRAService jiraService, TransactionService transactionService) {
        this.managementService = managementService;
        this.propertiesService = propertiesService;
        this.subversionService = subversionService;
        this.jiraService = jiraService;
        this.transactionService = transactionService;
    }

    @Override
    @Transactional(readOnly = true)
    public ChangeLogSummary getChangeLogSummary(int branchId, int from, int to) {
        try (Transaction ignored = transactionService.start()) {
            // Gets the branch
            BranchSummary branch = managementService.getBranch(branchId);
            // Gets the build information
            SVNBuild buildFrom = getBuild(from);
            SVNBuild buildTo = getBuild(to);
            // OK
            return new ChangeLogSummary(
                    UUID.randomUUID().toString(),
                    branch,
                    buildFrom,
                    buildTo
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ChangeLogRevisions getChangeLogRevisions(ChangeLogSummary summary) {
        // Finds the common ancestor of the two histories

        // Function that extracts the path from a SVN location
        Function<SVNReference, String> pathFn = new Function<SVNReference, String>() {
            @Override
            public String apply(SVNReference reference) {
                return reference.getPath();
            }
        };

        // List of paths on both histories
        List<String> pathsFrom = Lists.transform(summary.getBuildFrom().getHistory().getReferences(), pathFn);
        List<String> pathsTo = Lists.transform(summary.getBuildTo().getHistory().getReferences(), pathFn);

        // Index in the upper history
        Pair<Integer, Integer> commonAncestor = null;
        for (int i = 0; i < pathsFrom.size(); i++) {
            String upperUrl = pathsFrom.get(i);
            // Index of this URL in the lower history
            int index = pathsTo.indexOf(upperUrl);
            // Found!
            if (index >= 0) {
                commonAncestor = Pair.of(i, index);
                break;
            }
        }

        // A common ancestor must be found
        if (commonAncestor == null) {
            throw new NoCommonAncestorException();
        }
        int fromAncestorIndex = commonAncestor.getLeft();
        int toAncestorIndex = commonAncestor.getRight();

        // Reference
        String referencePath = pathsFrom.get(fromAncestorIndex);
        long referenceStartRevision = summary.getBuildFrom().getHistory().getReferences().get(fromAncestorIndex).getRevision();
        long referenceEndRevision = summary.getBuildTo().getHistory().getReferences().get(toAncestorIndex).getRevision();

        // No difference?
        if (referenceEndRevision == referenceStartRevision) {
            return ChangeLogRevisions.none();
        }

        // Ordering of revisions (we must have start > end)
        if (referenceStartRevision < referenceEndRevision) {
            long t = referenceStartRevision;
            referenceStartRevision = referenceEndRevision;
            referenceEndRevision = t;
        }

        // SVN transaction
        try (Transaction ignored = transactionService.start()) {
            // List of log entries
            SVNLogEntryCollector logEntryCollector = new SVNLogEntryCollector();
            // SVN change log
            subversionService.log(
                    SVNUtils.toURL(subversionService.getURL(referencePath)),
                    SVNRevision.create(referenceStartRevision),
                    SVNRevision.create(referenceStartRevision),
                    SVNRevision.create(referenceEndRevision + 1),
                    true, // Stops on copy
                    false, // No path discovering (yet)
                    0L, // no limit
                    true, // Includes merged revisions
                    logEntryCollector
            );
            // Loops through all SVN log entries, taking the merged revisions into account
            int level = 0;
            List<ChangeLogRevision> revisions = new ArrayList<>();
            for (SVNLogEntry svnEntry : logEntryCollector.getEntries()) {
                long revision = svnEntry.getRevision();
                if (SVNRevision.isValidRevisionNumber(revision)) {
                    // Conversion
                    ChangeLogRevision entry = createChangeLogRevision(level, svnEntry);
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
                collectIssuesForRevision(issues, revision);
            }
            // List of issues
            List<ChangeLogIssue> issuesList = new ArrayList<>(issues.values());
            // TODO Validation
            // validationService.validate(changeLog, issuesList);
            // OK
            return new ChangeLogIssues(issuesList);

        }
    }

    private void collectIssuesForRevision(Map<String, ChangeLogIssue> issues, long revision) {
        // Gets all issues attached to this revision
        List<String> issueKeys = subversionService.getIssueKeysForRevision(revision);
        // For each issue
        for (String issueKey : issueKeys) {
            // Gets its details if not indexed yet
            ChangeLogIssue changeLogIssue = issues.get(issueKey);
            if (changeLogIssue == null) {
                changeLogIssue = getChangeLogIssue(issueKey);
            }
            // Existing issue?
            if (changeLogIssue != null) {
                // Attaches the revision to this issue
                SVNRevisionInfo issueRevision = subversionService.getRevisionInfo(revision);
                changeLogIssue = changeLogIssue.addRevision(issueRevision);
                // Puts back into the cache
                issues.put(issueKey, changeLogIssue);
            }
        }
    }

    private ChangeLogIssue getChangeLogIssue(String issueKey) {
        // Gets the details about the JIRA issue
        try {
            JIRAIssue issue = jiraService.getIssue(issueKey);
            if (issue == null || StringUtils.isBlank(issue.getKey())) {
                return null;
            }
            // Creates the issue details for the change logs
            return new ChangeLogIssue(issue);
        } catch (JIRAIssueNotFoundException ex) {
            return null;
        }
    }

    private ChangeLogRevision createChangeLogRevision(int level, SVNLogEntry svnEntry) {
        long revision = svnEntry.getRevision();
        String message = svnEntry.getMessage();
        // Formatted message
        String formattedMessage = jiraService.insertIssueUrlsInMessage(message);
        // Revision URL
        String revisionUrl = subversionService.getRevisionBrowsingURL(revision);
        // OK
        return new ChangeLogRevision(
                level,
                revision,
                svnEntry.getAuthor(),
                subversionService.formatRevisionTime(new DateTime(svnEntry.getDate())),
                message,
                revisionUrl,
                formattedMessage);
    }

    private SVNBuild getBuild(int buildId) {
        // Gets the build basic information
        BuildSummary build = managementService.getBuild(buildId);
        // Gets the build SVN tag
        String buildPath = getBuildPath(build.getBranch().getId(), build.getName());
        // Gets the history for this tag using the SubversionService
        SVNHistory history = subversionService.getHistory(buildPath);
        // OK
        return new SVNBuild(
                build,
                history
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
